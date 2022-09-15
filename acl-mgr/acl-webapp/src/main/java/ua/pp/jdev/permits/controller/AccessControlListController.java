package ua.pp.jdev.permits.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.data.AccessControlList;
import ua.pp.jdev.permits.data.AccessControlListDAO;
import ua.pp.jdev.permits.data.Accessor;
import ua.pp.jdev.permits.data.Page;
import ua.pp.jdev.permits.enums.OrgLevel;
import ua.pp.jdev.permits.enums.Permit;
import ua.pp.jdev.permits.enums.State;
import ua.pp.jdev.permits.enums.XPermit;
import ua.pp.jdev.permits.service.DictionaryService;

@Controller
@Slf4j
@SessionAttributes("acl")
@RequestMapping("/acls")
public class AccessControlListController {
	private AccessControlListDAO aclDAO;
	private DictionaryService dictService;
	
	// TODO Make it configurable
	private final static int DEFAULT_PAGE_SIZE = 5;

	@Autowired
	private void setAccessControlListDAO(AccessControlListDAO aclDAO) {
		this.aclDAO = aclDAO;
	}

	@Autowired
	private void setDictionaryServiceControlListDAO(DictionaryService dictService) {
		this.dictService = dictService;
	}

	@ModelAttribute("acl")
	private AccessControlList putNewAclToModel() {
		return new AccessControlList(State.NEW);
	}

	@ModelAttribute("dictObjTypes")
	private Map<String, String> getDictObjTypes() {
		return dictService.getObjTypes();
	}

	private Map<String, String> getDictStatuses(Collection<String> objTypes) {
		return dictService.getStatuses(objTypes);
	}

	@ModelAttribute("dictPermits")
	private Map<String, Permit> getDictPermits() {
		return Arrays.asList(Permit.values()).stream()
				.collect(Collectors.toMap(t -> String.valueOf(t.getValue()), Function.identity()));
	}

	private Map<String, XPermit> getDictXPermits() {
		return Arrays.asList(XPermit.values()).stream()
				.collect(Collectors.toMap(t -> String.valueOf(t.getValue()), Function.identity()));
	}

	private Map<String, String> getAvailableAliases(boolean alias, boolean svc, AccessControlList acl) {
		Map<String, String> aliases = dictService.getAliases(alias, svc);
		return new TreeMap<>(aliases.entrySet().stream().filter(t -> !acl.hasAccessor(t.getKey()))
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
	}
	
	private Map<String, OrgLevel> getDictOrgLevels() {
		return Arrays.asList(OrgLevel.values()).stream()
				.collect(Collectors.toMap(t -> String.valueOf(t.getValue()), Function.identity()));
	}

	@GetMapping
	public String viewAllForm(@RequestParam(required = false) Integer pageNo, Model model, SessionStatus sessionStatus) {
		if (aclDAO.pageable()) {
			if (pageNo == null) {
				pageNo = 1;
			}
			
			Page<AccessControlList> page = aclDAO.readPage(pageNo - 1, DEFAULT_PAGE_SIZE);
			
			model.addAttribute("pageable", true);
			model.addAttribute("currentPage", pageNo);
		    model.addAttribute("totalPages", page.getPageCount());
		    model.addAttribute("totalItems", page.getItemCount());
		    model.addAttribute("acls", page.getContent());
			
		} else {
			model.addAttribute("pageable", false);
			model.addAttribute("acls", aclDAO.readAll());
		}

		// Clear previous session data
		sessionStatus.setComplete();

		return "acls";
	}

	@GetMapping("/favicon.svg")
	public String forwardFavicon() {
		// Forward favicon.svg request to a valid location
		return "forward:/favicon.svg";
	}

	@GetMapping("/new")
	public String newForm(@ModelAttribute("acl") AccessControlList acl, Model model) {
		model.addAttribute("httpMethod", "post");
		model.addAttribute("dictStatuses", getDictStatuses(acl.getObjTypes()));
		return "editACL";
	}

	@GetMapping("/{id}")
	public String viewForm(@PathVariable("id") String id, @RequestParam(required = false) String accessorName, Model model) {
		log.debug("Starting get ACL with ID={} for view", id);

		AccessControlList acl = readACL(id);
		model.addAttribute("acl", acl);
		model.addAttribute("dictStatuses", getDictStatuses(acl.getObjTypes()));

		if (accessorName != null && accessorName.length() > 0) {
			log.debug("Starting prepare eview form for Accessor '{}' of ACL {}", accessorName, acl);

			Optional<Accessor> optlAccessor = acl.getAccessor(accessorName);
			if (optlAccessor.isEmpty()) {
				// TODO Implement it!
				throw new RuntimeException();
			}

			model.addAttribute("accessor", optlAccessor.get());
			model.addAttribute("dictXPermits", getDictXPermits());
			model.addAttribute("dictOrgLevels", getDictOrgLevels());

			log.debug("Return view form for Accessor {}: ", optlAccessor.get());

			return "viewAccessor";
		}
		log.debug("Return view form for ACL {}: ", acl);

		return "viewACL";
	}

	protected AccessControlList readACL(String id) {
		Optional<AccessControlList> optionalAcl = aclDAO.read(id);
		if (optionalAcl.isEmpty()) {
			// TODO Implement it!
			throw new RuntimeException();
		}

		return optionalAcl.get();
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable("id") String id, @RequestParam(required = false) String accessorName,
			@RequestParam(required = false) boolean addAccessor, Model model) {
		AccessControlList acl = (AccessControlList) model.getAttribute("acl");
		if (!acl.getId().equalsIgnoreCase(id)) {
			acl = readACL(id);
			model.addAttribute("acl", acl);
		}

		if (addAccessor || (accessorName != null && accessorName.length() > 0)) {
			log.debug("Starting prepare edit form for Accessor '{}' of ACL {}", accessorName, acl);

			Accessor accessor;
			if (!addAccessor) {
				Optional<Accessor> optAccessor = acl.getAccessor(accessorName);
				if (optAccessor.isEmpty()) {
					// TODO Implement it!
					throw new RuntimeException();
				}
				accessor = optAccessor.get();

				model.addAttribute("accessor", accessor);
			} else {
				accessor = new Accessor(State.NEW);
				accessor.setName("");
				model.addAttribute("accessor", accessor);
			}

			model.addAttribute("dictAccessorNames", getAvailableAliases(accessor.isAlias(), accessor.isSvc(), acl));
			model.addAttribute("dictXPermits", getDictXPermits());
			model.addAttribute("dictOrgLevels", getDictOrgLevels());
			log.debug("Return edit form for Accessor {}: ", accessor);

			return "editAccessor";
		}
		log.debug("Starting prepare edit form for ACL {}", acl);

		model.addAttribute("dictStatuses", getDictStatuses(acl.getObjTypes()));

		if (acl.getId() == null || acl.getId().length() == 0) {
			model.addAttribute("httpMethod", "post");
		}
		log.debug("Return edit form for ACL {}: ", acl);

		return "editACL";
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") String id, @RequestParam(required = false) String accessorName, Model model) {
		if (accessorName != null && accessorName.length() > 0) {
			log.debug("Starting delete accessor '{}' from ACL with ID={}", accessorName, id);

			AccessControlList acl = (AccessControlList) model.getAttribute("acl");
			Optional<Accessor> optAccessor = acl.getAccessor(accessorName);
			if (optAccessor.isPresent()) {
				Accessor accessor = optAccessor.get();
				if (State.VOID.equals(accessor.getState())) {
					accessor.setState(State.DIRTY);
				} else {
					accessor.setState(State.VOID);
				}
			}
			log.info("Deleted accessor '{}' from ACL: {}", accessorName, acl);

			return String.format("redirect:/acls/%s/edit", acl.getId());
		}

		log.debug("Starting delete ACL with ID={}", id);

		if (aclDAO.delete(id)) {
			log.info("ACL with ID={} successfully deleted", id);
		} else {
			log.warn("ACL with ID={} was not deleted", id);
		}

		return "redirect:/acls";
	}

	@PostMapping()
	public String create(@RequestParam(required = false) boolean addAccessor,
			@Valid @ModelAttribute("acl") AccessControlList acl, BindingResult errors, Model model) {
		if (addAccessor) {
			Accessor newAccessor = new Accessor(State.NEW);
			model.addAttribute("accessor", newAccessor);
			
			model.addAttribute("dictAccessorNames", getAvailableAliases(newAccessor.isAlias(), newAccessor.isSvc(), acl));
			model.addAttribute("dictXPermits", getDictXPermits());
			model.addAttribute("dictOrgLevels", getDictOrgLevels());
			return "editAccessor";
		}

		log.debug("Starting create new ACL " + acl);
		
		model.addAttribute("dictStatuses", getDictStatuses(acl.getObjTypes()));
		
		// Check whether another ACL with the same name is already existed
		// and register corresponding validation error if it's true
		Optional<AccessControlList> optACL = aclDAO.readByName(acl.getName());
		if(optACL.isPresent()) {
			errors.addError(new FieldError("acl", "name", acl.getName(), false, null, null, "ACL name must be unique!"));
		}

		if (errors.hasErrors()) {
			log.debug("Failed to create new ACL " + acl + " due to validation errors " + errors.toString());
			model.addAttribute("httpMethod", "post");
			return "editACL";
		}

		aclDAO.create(acl);
		log.info("Created new ACL: " + acl);

		return "redirect:/acls";
	}

	@PatchMapping
	public String updateACL(@Valid @ModelAttribute("acl") AccessControlList acl, BindingResult errors, Model model) {
		log.debug("Starting update ACL " + acl);
		
		// Check whether another ACL with the same name is already existed 
		// and register corresponding validation error if it's true
		Optional<AccessControlList> optACL = aclDAO.readByName(acl.getName());
		if(optACL.isPresent() && !optACL.get().getId().equalsIgnoreCase(acl.getId())) {
			errors.addError(new FieldError("acl", "name", acl.getName(), false, null, null, "ACL name must be unique!"));
		}

		if (errors.hasErrors()) {
			log.debug("Failed to update ACL " + acl + " due to validation errors " + errors.toString());
			
			model.addAttribute("dictStatuses", getDictStatuses(acl.getObjTypes()));
			return "editACL";
		}

		aclDAO.update(acl);
		log.info("Updated ACL: " + acl);

		return "redirect:/acls";
	}

	@PatchMapping("/{id}")
	public String updateAccessor(@RequestParam boolean refresh, @RequestParam String accessorName,
			@Valid @ModelAttribute("accessor") Accessor accessor, BindingResult errors, Model model) {
		log.debug("Starting update Accessor {}", accessor);
		
		AccessControlList acl = (AccessControlList) model.getAttribute("acl");
		
		if (errors.hasErrors() || refresh) {
			if (errors.hasErrors()) {
				log.debug("Failed to update Accessor {} due to validation errors {}", accessor, errors.toString());
			}

			model.addAttribute("dictAccessorNames", getAvailableAliases(accessor.isAlias(), accessor.isSvc(), acl));
			model.addAttribute("dictXPermits", getDictXPermits());
			model.addAttribute("dictOrgLevels", getDictOrgLevels());
			return "editAccessor";
		}

		if (State.PURE.equals(accessor.getState())) {
			accessor.setState(State.DIRTY);
		}

		
		acl.addAccessor(accessor);
		log.info("Updated Accessor: {}", accessor);

		return String.format("redirect:/acls/%s/edit", acl.getId());
	}
}
