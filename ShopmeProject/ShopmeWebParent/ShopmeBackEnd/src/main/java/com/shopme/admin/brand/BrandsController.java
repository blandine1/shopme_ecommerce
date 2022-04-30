package com.shopme.admin.brand;

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

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandsController {

	@Autowired
	private BrandService service;
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/brands")
	public String listBrands(Model model) {
		return listByPage(1, model, "name", "asc", null);
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, Model model,
			                 @Param("sortField") String sortField, @Param("sortDir") String sortDir,
			                 @Param("keyword") String keyword) {
		
		Page<Brand> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<Brand> listBrands = page.getContent();
		
		long startCount = (pageNum - 1) * BrandService.BRANDS_PER_PAGE + 1;
		long endCount = startCount + BrandService.BRANDS_PER_PAGE + 1;
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
       String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("reverseSortDir", reverseSortDir);
		
		return "brand/brands";
	}
	
	@GetMapping("/brands/new")
	public String newBrands(Model model) {
		List<Category> listCategoriesUsedInForm = categoryService.listCategoriesUsedInForm();
		
		model.addAttribute("brand", new Brand() );
		model.addAttribute("listCategoriesUsedInForm", listCategoriesUsedInForm);
		model.addAttribute("pageTitle", "new brand");
		return "brand/brand_form";
	}
	
	@PostMapping("/brands/save")
	public String saveBrands(Brand brand ,Model model, @RequestParam("fileImage") MultipartFile file ,RedirectAttributes attributes) throws IOException {
		 if(!file.isEmpty()) {
			 String fileName = StringUtils.cleanPath(file.getOriginalFilename().replace(" ", "_"));
			 String name = brand.getName();
			 brand.setName(name.substring(0, 1).toUpperCase() + name.substring(1));
			 brand.setLogo(fileName.replace(" ", "_"));
			 
			 
			 Brand savedBrand = service.save(brand);
			 String uploadDir  = "../brand-logo/"+savedBrand.getId();
			 FileUploadUtil.cleanDir(uploadDir);
			 FileUploadUtil.saveFile(uploadDir, fileName, file);
		 }else {
			 service.save(brand);
		 }
		 
		 attributes.addFlashAttribute("message", "brand is saved successfully");
		
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editBrands(@PathVariable(name = "id") Integer id ,Model model, RedirectAttributes attributes) throws BrandNotFoutException {
		try {
			Brand brand = service.get(id);
			List<Category> listCategoriesUsedInForm = categoryService.listCategoriesUsedInForm();
			
			
			model.addAttribute("brand", brand);
			model.addAttribute("listCategoriesUsedInForm", listCategoriesUsedInForm);
			model.addAttribute("pageTitle", "editing brand with id "+ id);
			
			return "brand/brand_form";
		} catch (BrandNotFoutException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/brands";
	}
	
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrands(@PathVariable("id") Integer id ,Model model, RedirectAttributes attributes) throws BrandNotFoutException {
		
		try {
			service.delete(id);
			String brandDir = "../brand-logo/"+id;
			System.out.println(brandDir);
			FileUploadUtil.cleanDir(brandDir);

			
			attributes.addFlashAttribute("message", "the brand id "+ id + " has been deleted successfully");
		} catch (BrandNotFoutException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			
		}
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/csv")
	public void brandCsvPrinter(HttpServletResponse response) throws IOException {
		Iterable<Brand> brands = service.listAll();
		
		BrandCsvExporter exporter = new BrandCsvExporter();
		exporter.export(brands, response);
	}
	
	
}
