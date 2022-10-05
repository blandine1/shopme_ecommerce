package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.AmazonS3Util;
import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.user.UserNotFountException;
import com.shopme.admin.user.UserServices;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {

	@Autowired
	private UserServices services;

	@GetMapping("/users")
	public String listFirstPage() {
		return "redirect:/users/page/1?sortField=firstName&sortDir=asc";
	}

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName="listUsers", moduleUrl="/users") PagingAndSortingHelper helper,
			@PathVariable("pageNum") int pageNum, Model model) {
		services.listByPage(pageNum, helper);
	
		

		return "users/users";
	}

	@GetMapping("/users/new")
	public String neUserForm(Model model) {
		List<Role> listRoles =services.listRoles();
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "create new user");

		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes attributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {

		//System.out.println("account nzw user   :"+user.getPassword());
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename().replace(" ", "_"));
			user.setPhotos(fileName);
			User savedUser =services.save(user);

			String uploadDir = "user-photos/"+ savedUser.getId();
			
			AmazonS3Util.removeFolder(uploadDir);
			AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());

			//FileUploadUtil.cleanDir(uploadDir);
			//FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}else {
			if(user.getPhotos().isEmpty())
				user.setPhotos(null);
			   services.save(user);
		}
		//User savedUser =services.save(user);

		attributes.addFlashAttribute("message", "user is saved successfully");

		return getRedirectUrlToAffectedUser(user);
	}

	private String getRedirectUrlToAffectedUser(User user) {
		String firstPartOfEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" +firstPartOfEmail;
	}

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, Model model ,RedirectAttributes attributes) throws UserNotFountException {
		try {
		   User user=services.get(id);
		   List<Role> listRoles =services.listRoles();
		   model.addAttribute("user", user);
		   model.addAttribute("pageTitle", "Edit user Id "+id);
		   model.addAttribute("listRoles", listRoles);

		   return "users/user_form";

		}catch(UserNotFountException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/users";
		}
	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes attributes) {

		try {
			services.deleteById(id);
			String deletPhotoDir = "user-photos/"+id;
			
			AmazonS3Util.removeFolder(deletPhotoDir);
			
			//FileUploadUtil.cleanDir(deletDir);
			
			attributes.addFlashAttribute("message", "user with id "+ id +" is deleted successfully");
		} catch (UserNotFountException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());

		}

		return "redirect:/users";
	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnableStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes attributes,
			Model model) {
		services.updateUserStatus(id, enabled);
		String status = enabled ? "enabled" : "desabled";
		String message = "this user "+ id +" has been "+status;

		attributes.addFlashAttribute("message", message);
		return "redirect:/users";
	}

	@GetMapping("/users/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		System.out.println("la reponse du serveur est ......");
		List<User> listUsers = services.listAll();
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(listUsers, response);
	}

	@GetMapping("/users/export/excel")
	public void exportUsersToExcel(HttpServletResponse response) throws IOException {
		List<User> listUsers = services.listAll();
		UserExcelExporter exporter = new UserExcelExporter();
		exporter.export(listUsers, response);
	}

	@GetMapping("/users/export/pdf")
	public void exportUsersToPdf(HttpServletResponse response) throws IOException {
		List<User> listUsers = services.listAll();
		UserPdfExporter exporter = new UserPdfExporter();
		exporter.export(listUsers, response);
	}
}
