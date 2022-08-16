package ua.pp.jdev.permits.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

	@Autowired
	@Qualifier("springDataJdbcAclDAO")
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

	@ModelAttribute("dictStatuses")
	private Map<String, String> getDictStatuses() {
		return dictService.getStatuses();
	}
	
	@ModelAttribute("dictPermits")
	private Map<String, Permit> getDictPermits() {
		return Arrays.asList(Permit.values()).stream().collect(Collectors.toMap(t -> String.valueOf(t.getValue()), Function.identity()));
	}

	private Map<String, XPermit> getDictXPermits() {
		return Arrays.asList(XPermit.values()).stream().collect(Collectors.toMap(t -> String.valueOf(t.getValue()), Function.identity()));
	}
	
	private Map<String, OrgLevel> getDictOrgLevels() {
		return Arrays.asList(OrgLevel.values()).stream().collect(Collectors.toMap(t -> String.valueOf(t.getValue()), Function.identity()));
	}
	
	@GetMapping
	public String viewAllForm(Model model, SessionStatus sessionStatus) {
		model.addAttribute("acls", aclDAO.readAll());

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
	public String newForm(Model model) {
		model.addAttribute("httpMethod", "post");
		return "editACL";
	}
	
	@GetMapping("/{id}")
	public String viewForm(@PathVariable("id") String id, @RequestParam(required = false) String accessorName, Model model) {
		log.debug("Starting get ACL with ID={} for view", id);

		AccessControlList acl = readACL(id);
		model.addAttribute("acl", acl);

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
	public String editForm(@PathVariable("id") String id, @RequestParam(required = false) String accessorName, @RequestParam(required = false) boolean addAccessor, Model model) {
		AccessControlList acl = (AccessControlList) model.getAttribute("acl");
		if(!acl.getId().equalsIgnoreCase(id)) {
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

			model.addAttribute("dictXPermits", getDictXPermits());
			model.addAttribute("dictOrgLevels", getDictOrgLevels());
			log.debug("Return edit form for Accessor {}: ", accessor);

			return "editAccessor";
		}
		log.debug("Starting prepare edit form for ACL {}", acl);

		if (acl.getId() == null || acl.getId().length() == 0) {
			model.addAttribute("httpMethod", "post");
		}
		log.debug("Return edit form for ACL {}: ", acl);

		return "editACL";
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") String id, @RequestParam(required = false) String accessorName,
			Model model) {
		if (accessorName != null && accessorName.length() > 0) {
			log.debug("Starting delete accessor '{}' from ACL with ID={}", accessorName, id);

			AccessControlList acl = (AccessControlList) model.getAttribute("acl");
			Optional<Accessor> optional = acl.getAccessor(accessorName);
			if (optional.isPresent()) {
				optional.get().setState(State.VOID);
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
		log.debug("Starting create new ACL " + acl);

		if (addAccessor) {
			model.addAttribute("accessor", new Accessor(State.NEW));
			return "redirect:/acls";
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
	public String updateACL(@Valid @ModelAttribute("acl") AccessControlList acl, BindingResult errors) {
		log.debug("Starting update ACL " + acl);

		if (errors.hasErrors()) {
			log.debug("Failed to update ACL " + acl + " due to validation errors " + errors.toString());
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

		if (errors.hasErrors() || refresh) {
			if (errors.hasErrors()) {
				log.debug("Failed to update Accessor {} due to validation errors {}", accessor, errors.toString());
			}

			model.addAttribute("dictXPermits", getDictXPermits());
			model.addAttribute("dictOrgLevels", getDictOrgLevels());
			return "editAccessor";
		}

		if (State.PURE.equals(accessor.getState())) {
			accessor.setState(State.DIRTY);
		}

		AccessControlList acl = (AccessControlList) model.getAttribute("acl");
		acl.addAccessor(accessor);
		log.info("Updated Accessor: {}", accessor);

		return String.format("redirect:/acls/%s/edit", acl.getId());
	}
}
