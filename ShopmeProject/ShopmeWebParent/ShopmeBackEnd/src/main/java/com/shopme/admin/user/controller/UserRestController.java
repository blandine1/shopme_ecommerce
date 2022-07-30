package com.shopme.admin.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.user.UserServices;

@RestController
public class UserRestController {

	@Autowired
	private UserServices services;

	@PostMapping("/users/check_email")
	public String checkDuplicateEmail(Integer id, String email) {

		return  services.isEmailUnique(id, email) ? "Ok" : "Duplicated";
	}
}
