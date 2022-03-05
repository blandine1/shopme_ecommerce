package com.shopme.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {
	
	@Autowired
	private UserServices services;
	
	@GetMapping("/users")
	public String listAllUsers(Model model) {
		List<User> listUsers = services.listAll();
		model.addAttribute("listUsers", listUsers);
		
		return "users";
	}
	
	@GetMapping("/users/new")
	public String neUserForm(Model model) {
		List<Role> listRoles =services.listRoles();
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		
		return "user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes attributes) {
		System.out.println(user);
		User savedUser =services.save(user);
		
		attributes.addFlashAttribute("message", "user "+savedUser.getFirstName()+ " is saved successfully");
		return "redirect:/users";
	}

}
