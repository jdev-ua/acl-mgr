package ua.pp.jdev.permits.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import ua.pp.jdev.permits.data.Page;
import ua.pp.jdev.permits.data.User;
import ua.pp.jdev.permits.data.UserDAO;
import ua.pp.jdev.permits.enums.Role;

@Slf4j
@Controller
@SessionAttributes("user")
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
	private UserDAO userDAO;

	// TODO Make it configurable
	private final static int DEFAULT_PAGE_SIZE = 5;

	@Autowired
	private void setAccessControlListDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@ModelAttribute("user")
	private User name() {
		return new User();
	}

	@ModelAttribute("dictRoles")
	private Map<String, Role> getDictRoles() {
		return Arrays.asList(Role.values()).stream().collect(Collectors.toMap(Role::getFullName, Function.identity()));
	}

	@ModelAttribute("listRoles")
	private List<Role> getListRoles() {
		return List.of(Role.values());
	}

	@GetMapping("/favicon.svg")
	public String forwardFavicon() {
		// Forward favicon.svg request to a valid location
		return "forward:/favicon.svg";
	}

	@GetMapping()
	public String viewAllForm(@RequestParam(defaultValue = "1") Integer pageNo, Model model,
			SessionStatus sessionStatus) {
		Page<User> page = userDAO.readPage(pageNo - 1, DEFAULT_PAGE_SIZE);

		model.addAttribute("pageable", true);
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getPageCount());
		model.addAttribute("totalItems", page.getItemCount());
		model.addAttribute("users", page.getContent());

		// Clear previous session data
		sessionStatus.setComplete();

		return "users";
	}

	@GetMapping("/new")
	public String newForm(Model model) {
		model.addAttribute("httpMethod", "post");
		return "editUser";
	}

	@GetMapping("/{id}")
	public String viewForm(@PathVariable("id") String id, Model model) {
		Optional<User> optUser = userDAO.read(id);
		if (optUser.isEmpty()) {
			// TODO Implement it!
			throw new RuntimeException("Hueston, we have a problem there..");
		}
		model.addAttribute("user", optUser.get());

		return "editUser";
	}

	@PostMapping("/{id}")
	public String create(@Valid @ModelAttribute("user") User user, BindingResult errors, Model model) {
		log.debug("Starting create new User {}", user);

		if (errors.hasErrors()) {
			log.debug("Failed to create new User {} due to validation errors {}", user, errors.toString());
			model.addAttribute("httpMethod", "post");
			return "editUser";
		}

		userDAO.create(user);
		log.info("Created new User: {}", user);

		return "redirect:/users";
	}

	@PatchMapping("/{id}")
	public String update(@Valid @ModelAttribute("user") User user, BindingResult errors, Model model) {
		log.debug("Starting update User {}", user);

		if (errors.hasErrors()) {
			log.debug("Failed to update User {} due to validation errors {}", user, errors.toString());
			return "editUser";
		}

		userDAO.update(user);
		log.info("Updated User: {}", user);

		return "redirect:/users";
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") String id, Model model) {
		log.debug("Starting delete User with ID={}", id);

		userDAO.delete(id);
		log.info("User with ID={} successfully deleted", id);

		return "redirect:/users";
	}
}
