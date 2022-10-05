package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;

@Controller
public class CategoryController {


	@Autowired
	private CategoryService categoryService;
 
	@GetMapping("/categories")
	public String categories(@Param("sortDir") String sortDir, Model model) {
		return listByPage(1, sortDir, null ,model);
	}
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum,@Param("sortDir") String sortDir ,
			@Param("keyword") String keyword
			,Model model) {
		if(sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		Iterable<Category> listCategories = categoryService.listByPage(pageInfo, pageNum,sortDir, keyword);
		System.out.println("totalElements : "+pageInfo.getTotalElements());
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        
        long startCount = (pageNum -1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
		if(endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
        
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", "name");
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("moduleUrl", "/categories");
		
		model.addAttribute("listCategories", listCategories);

		model.addAttribute("reverseSortDir", reverseSortDir);
		return "categories/categories";
	}
 
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		model.addAttribute("category", new Category());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "create new category");

		return "categories/category_form";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category, Model  model,@RequestParam("fileImage") MultipartFile multipartFile , RedirectAttributes attributes) throws IOException {
		
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);
			String name = category.getName();
			category.setName(name.substring(0, 1).toUpperCase() + name.substring(1));
			
			Category savedCategory = categoryService.saveCategory(category);
			
			String uploadDir = "category-image/"+ savedCategory.getId();
			
			AmazonS3Util.removeFolder(uploadDir);
			AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
			
			//FileUploadUtil.cleanDir(uploadDir);
		    //FileUploadUtil.saveFile(uploadDir,fileName, multipartFile);
		}else {
			//if(category.getImage().isEmpty()) {
				//category.setImage(null);
				categoryService.saveCategory(category);
			//}
			
		}
		
		attributes.addFlashAttribute("message", "category is saved successfully");
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/edit/{id}")
	public String edidtCategory(@PathVariable(name = "id") Integer id,Model model, RedirectAttributes attributes) {
		try {
			Category category = categoryService.get(id);
			List<Category> listCategories = categoryService.listCategoriesUsedInForm();
			model.addAttribute("category", category);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "you are editing category with id : "+id);
			
			return "categories/category_form";
		} catch (CategoryNotFoundException e) {
			attributes.addAttribute("message", e.getMessage());
			
			return "redirect:/categories";
		}
		
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateCategoryStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes attributes) {
		categoryService.updateCategoryEnabledStatus(id, enabled);
		
		String status = enabled ? "enabled" : "desabled"; 
		
		String message ="this category ID "+id+" has been "+status;
		
		attributes.addFlashAttribute("message", message);
		
		return "redirect:/categories";
		
	}
	
	@GetMapping("/categories/delete/{id}")
	public String deleteCatgory(@PathVariable("id") Integer id ,Model model,RedirectAttributes attributes) {
		try {
			categoryService.deleteCategory(id);
			String catgoryDir = "category-image/"+id;
			AmazonS3Util.removeFolder(catgoryDir);
			
			attributes.addFlashAttribute("message", "the category whit id "+id+" has been deleted successfully");
		} catch (CategoryNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/csv")
	public void eportToCSV(HttpServletResponse response) throws IOException {
		List<Category> categories = categoryService.listCategoriesUsedInForm();
		CategoriesCsvExporter export = new CategoriesCsvExporter();
		export.export(categories, response);
	}

}
