package com.shopme.admin;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {


	@GetMapping("")
	public String viewHomePage() {
		return "index";
	}

	@GetMapping("/login")
	public String viewLoginPage() {

		//ici on va rediriger le login page en fonction de si un user est deja connecte ou non
		Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}
		
		return "redirect:/";
	}
}
