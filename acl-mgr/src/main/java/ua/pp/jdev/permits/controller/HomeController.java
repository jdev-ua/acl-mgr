package ua.pp.jdev.permits.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	@GetMapping({ "/", "/home" })
	public String home() {
		return "index";
	}

	@GetMapping("/rules")
	public String rules() {
		return "index";
	}
}
