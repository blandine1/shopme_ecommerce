package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;
import com.shopme.common.exception.ProductNotFountException;

@Controller
public class ProductController {

	
	
	@Autowired
	private ProductService service;
	
	@Autowired
	public BrandService brandService;
	
	@Autowired
	public CategoryService categoryService;
	
	@GetMapping("/products")
	public String listFirstPage(Model model) {
		
		return listByPage(1, model, "name", "asc", null, 0);
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, Model model,
			                 @Param("sortField") String sortField, @Param("sortDir") String sortDir,
			                 @Param("keyword") String keyword,
			                 @Param("categoryId") Integer categoryId
			                 ) {
		
		System.out.println("seletcted Id category " +categoryId);
		Page<Product> page = service.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
		List<Product> listProducts = page.getContent();
		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		long startCount = (pageNum - 1) * ProductService.PRODUCT_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCT_PER_PAGE + 1;
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		if(categoryId != null) model.addAttribute("categoryId", categoryId);
		
       String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("listCategories", listCategories);
		
		return "products/products";
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		Iterable<Brand> listBrands = brandService.listAll();
		
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("product", product);
		model.addAttribute("pageTitle", "Create new product");
		model.addAttribute("numberOfExistingExtraImage", 0);
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes attributes, 
			                  @RequestParam(value="fileImage", required=false) MultipartFile mainImageMultiPart,
			                  @RequestParam(value="extraImage", required=false) MultipartFile[] extraImageMultipart,
			                  @RequestParam(name="detailIDs", required=false) String[] detailIDs,
			                  @RequestParam(name="detailNames", required=false) String[] detailNames,
			                  @RequestParam(name="detailValues", required=false) String[] detailValues,
			                  @RequestParam(name="imageIDs", required=false) String[] imageIDs,
			                  @RequestParam(name="imageNames", required=false) String[] imageNames,
			                  @AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {
		//System.out.println("//////////////////"+product.getDiscountPercent());
		if(loggedUser.hasRole("Salesperson")) {
			service.saveProductPrice(product);
			
		    attributes.addFlashAttribute("message", "product has been saved successfully");
				
		    return "redirect:/products";
		}
		
		ProductSaveHelper.setMainImageName(mainImageMultiPart, product);
		ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
		ProductSaveHelper.setNewExtraImageNames(extraImageMultipart, product);
		ProductSaveHelper.setProductDetails(detailIDs,detailNames, detailValues, product);
		
		
			
			Product savedProduct = service.save(product);
			
			ProductSaveHelper.saveUploadImages(mainImageMultiPart, extraImageMultipart, savedProduct);
			
			ProductSaveHelper.deleteExtraImageRemoveInForm(product);
			
		    attributes.addFlashAttribute("message", "product has been saved successfully");
		
		return "redirect:/products";
	}
	
	
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, 
			RedirectAttributes attributes, Model model) {
		service.updateProductSatus(id, enabled);
		
		String status = enabled ? "enabled" : "desabled" ;
		String message = "product id "+id+" has been "+ status;
		
		
		attributes.addFlashAttribute("status", status);
		attributes.addFlashAttribute("message", message);
		return "redirect:/products";
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes attributes,Model model) {
		try {
			service.deleteProduct(id);
			String uploadExtraImageDir = "../product-images/"+ id + "/extras";//extra images
			String uploadImageDir = "../product-images/"+ id;//the main image
			FileUploadUtil.removeDir(uploadExtraImageDir);
			FileUploadUtil.removeDir(uploadImageDir);
			
			attributes.addFlashAttribute("message", "the product with Id "+id+ " has been deleted");
		} catch (ProductNotFountException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/products";
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes attributes)  {
		try {
			Product product = service.get(id);
			
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", "editing product ID " + id);
			Integer numberOfExistingExtraImage = product.getImages().size();
			 //System.out.println("numberOfExistingExtraImage "+numberOfExistingExtraImage);
			//recuperer la liste des brands
			Iterable<Brand> listBrands = brandService.listAll();
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("numberOfExistingExtraImage", numberOfExistingExtraImage);
			
			return "products/product_form";
		} catch (ProductNotFountException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			//si pas trouve on retourne a la page principale
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/details/{id}")
	public String viewtProductDetail(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes attributes)  {
		try {
			Product product = service.get(id);
			
			model.addAttribute("product", product);
			
			return "products/product_detail_modal";
		} catch (ProductNotFountException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			//si pas trouve on retourne a la page principale
			return "redirect:/products";
		}
	}
}
