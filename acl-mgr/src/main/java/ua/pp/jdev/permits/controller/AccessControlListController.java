package ua.pp.jdev.permits.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import lombok.extern.slf4j.Slf4j;
import ua.pp.jdev.permits.dao.AccessControlListDAO;
import ua.pp.jdev.permits.domain.AccessControlList;
import ua.pp.jdev.permits.domain.Accessor;
import ua.pp.jdev.permits.enums.OrgLevel;
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
	public String acls(Model model, SessionStatus sessionStatus) {
		model.addAttribute("acls", aclDAO.readAll());

		// Clear previous session data
		sessionStatus.setComplete();

		return "acls";
	}

	@GetMapping("/new")
	public String newACL() {
		return "newACL";
	}

	@GetMapping("/{id}")
	public String viewACL(@PathVariable("id") String id, Model model) {
		model.addAttribute("acl", aclDAO.read(id));

		return "viewACL";
	}

	@GetMapping("/{id}/{accessor}")
	public String viewAccessor(@PathVariable("id") String id, @PathVariable("accessor") String accessorName, Model model) {
		AccessControlList acl = aclDAO.read(id);
		if (acl == null) {
			// TODO Implement it!
		}

		Optional<Accessor> result = acl.getAccessors().stream().filter(a -> a.getName().equals(accessorName)).findFirst();
		if (result.isEmpty()) {
			// TODO Implement it!
		}
		
		model.addAttribute("acl", acl);
		model.addAttribute("accessor", result.get());
		model.addAttribute("dictXPermits", Arrays.asList(XPermit.values()));
		model.addAttribute("dictOrgLevels", Arrays.asList(OrgLevel.values()));

		return "viewAccessor";
	}

	@GetMapping("/{id}/edit")
	public String editACL(@PathVariable("id") String id, Model model) {
		model.addAttribute("acl", aclDAO.read(id));

		return "editACL";
	}

	@DeleteMapping("/{id}")
	public String deleteACL(@PathVariable("id") String id, Model model) {
		log.debug("Starting delete ACL with ID=" + id);

		AccessControlList acl = aclDAO.delete(id);
		log.info("Deleted ACL: " + acl);

		return "redirect:/acls";
	}

	@PostMapping()
	public String submitNewACL(@Valid @ModelAttribute("acl") AccessControlList acl, BindingResult errors,
			SessionStatus sessionStatus) {
		log.debug("Starting create new ACL " + acl);

		if (errors.hasErrors()) {
			log.debug("Failed to create new ACL " + acl + " due to validation errors " + errors.toString());
			return "newACL";
		}

		aclDAO.create(acl);
		log.info("Created new ACL: " + acl);

		return "redirect:/acls";
	}

	@PatchMapping
	public String submitEditACL(@Valid @ModelAttribute("acl") AccessControlList acl, BindingResult errors,
			SessionStatus sessionStatus) {
		log.debug("Starting update ACL " + acl);

		if (errors.hasErrors()) {
			log.debug("Failed to update ACL " + acl + " due to validation errors " + errors.toString());
			return "editACL";
		}

		aclDAO.update(acl);
		log.info("Updated ACL: " + acl);

		return "redirect:/acls";
	}
}
