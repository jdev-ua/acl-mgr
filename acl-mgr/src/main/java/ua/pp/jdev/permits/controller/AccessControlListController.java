package ua.pp.jdev.permits.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

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
import ua.pp.jdev.permits.dao.AccessControlListDAO;
import ua.pp.jdev.permits.domain.AccessControlList;
import ua.pp.jdev.permits.domain.Accessor;
import ua.pp.jdev.permits.enums.OrgLevel;
import ua.pp.jdev.permits.enums.Permit;
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
	@Qualifier("xmlAclDAO")
	private void setAccessControlListDAO(AccessControlListDAO aclDAO) {
		this.aclDAO = aclDAO;
	}

	@Autowired
	private void setDictionaryServiceControlListDAO(DictionaryService dictService) {
		this.dictService = dictService;
	}

	@ModelAttribute("acl")
	private AccessControlList name() {
		return new AccessControlList();
	}

	@ModelAttribute("dictObjTypes")
	private Map<String, String> getDictObjTypes() {
		return dictService.getObjTypes();
	}

	@ModelAttribute("dictStatuses")
	private Map<String, String> getDictStatuses() {
		return dictService.getStatuses();
	}

	@GetMapping
	public String viewAllForm(Model model, SessionStatus sessionStatus) {
		model.addAttribute("acls", aclDAO.readAll());

		// Clear previous session data
		sessionStatus.setComplete();

		return "acls";
	}

	@GetMapping("/new")
	public String newForm(Model model) {
		model.addAttribute("httpMethod", "post");
		return "editACL";
	}

	@GetMapping("/{id}")
	public String viewForm(@PathVariable("id") String id, @RequestParam(required = false) String accessorName,
			Model model) {

		Optional<AccessControlList> optionalAcl = aclDAO.read(id);
		if (optionalAcl.isEmpty()) {
			// TODO Implement it!
		}
		AccessControlList acl = optionalAcl.get();
		model.addAttribute("acl", acl);

		if (accessorName != null && accessorName.length() > 0) {
			Optional<Accessor> optionalAccessor = acl.getAccessors().stream()
					.filter(a -> a.getName().equals(accessorName)).findFirst();
			if (optionalAccessor.isEmpty()) {
				// TODO Implement it!
			}

			model.addAttribute("accessor", optionalAccessor.get());
			model.addAttribute("dictPermits", Arrays.asList(Permit.values()));
			model.addAttribute("dictXPermits", Arrays.asList(XPermit.values()));
			model.addAttribute("dictOrgLevels", Arrays.asList(OrgLevel.values()));

			return "viewAccessor";
		}

		return "viewACL";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable("id") String id, @RequestParam(required = false) String accessorName,
			@RequestParam(required = false) boolean addAccessor, Model model) {
		AccessControlList acl = (AccessControlList) model.getAttribute("acl");

		if (addAccessor || (accessorName != null && accessorName.length() > 0)) {
			if (!addAccessor) {
				Optional<Accessor> result = acl.getAccessors().stream().filter(a -> a.getName().equals(accessorName))
						.findFirst();
				if (result.isEmpty()) {
					// TODO Implement it!
				}

				model.addAttribute("accessor", result.get());
			} else {
				Accessor dummy = new Accessor();
				dummy.setName("");
				model.addAttribute("accessor", dummy);
			}

			model.addAttribute("dictPermits", Arrays.asList(Permit.values()));
			model.addAttribute("dictXPermits", Arrays.asList(XPermit.values()));
			model.addAttribute("dictOrgLevels", Arrays.asList(OrgLevel.values()));

			return "editAccessor";
		}

		if (acl.getId() == 0) {
			model.addAttribute("httpMethod", "post");
		}

		return "editACL";
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") String id, @RequestParam(required = false) String accessorName,
			Model model) {
		if (accessorName != null && accessorName.length() > 0) {
			log.debug("Starting delete accessor '{}' from ACL with ID={}", accessorName, id);

			AccessControlList acl = (AccessControlList) model.getAttribute("acl");
			if (acl.hasAccessor(accessorName)) {
				acl.getAccessor(accessorName).markDeleted();
			}
			log.info("Deleted accessor '{}' from ACL: {}", accessorName, acl);

			return String.format("redirect:/acls/%s/edit", acl.getId());
		}

		log.debug("Starting delete ACL with ID={}", id);

		Optional<AccessControlList> result = aclDAO.delete(id);
		log.info("Deleted ACL: {}", result.isPresent() ? result.get() : ("ID=" + id));

		return "redirect:/acls";
	}

	@PostMapping()
	public String create(@RequestParam(required = false) boolean addAccessor,
			@Valid @ModelAttribute("acl") AccessControlList acl, BindingResult errors, Model model) {
		log.debug("Starting create new ACL " + acl);

		if (addAccessor) {
			model.addAttribute("accessor", new Accessor());
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

			model.addAttribute("dictPermits", Arrays.asList(Permit.values()));
			model.addAttribute("dictXPermits", Arrays.asList(XPermit.values()));
			model.addAttribute("dictOrgLevels", Arrays.asList(OrgLevel.values()));
			return "editAccessor";
		}

		AccessControlList acl = (AccessControlList) model.getAttribute("acl");
		acl.addAccessor(accessor);
		log.info("Updated Accessor: {}", accessor);

		return String.format("redirect:/acls/%s/edit", acl.getId());
	}
}
