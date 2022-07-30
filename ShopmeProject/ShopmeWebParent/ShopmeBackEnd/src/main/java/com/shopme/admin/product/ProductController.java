package com.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
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
		return "redirect:/products/page/1?sortField=name&sortDir=asc&categoryId=0";
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName ="listProducts", moduleUrl="/products" ) PagingAndSortingHelper helper,
			                 @PathVariable("pageNum") int pageNum, Model model,
			                 @Param("categoryId") Integer categoryId
			                 ) {
		
		System.out.println("seletcted Id category " +categoryId);
		service.listByPage(pageNum, helper, categoryId);
		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		if(categoryId != null) model.addAttribute("categoryId", categoryId);
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
		
		if(!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
		   if(loggedUser.hasRole("Salesperson")) {
			  service.saveProductPrice(product);
		      attributes.addFlashAttribute("message", "product has been saved successfully");
		      return "redirect:/products";
		    }
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
	public String editProduct(@PathVariable(name = "id") Integer id, Model model,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser
			, RedirectAttributes attributes)  {
		try {
			Product product = service.get(id);
			
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", "editing product ID " + id);
			Integer numberOfExistingExtraImage = product.getImages().size();
			
			Iterable<Brand> listBrands = brandService.listAll();
			boolean isReadOnlyForSaleSperson = false;
			
			if(!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
				   if(loggedUser.hasRole("Salesperson")) {
					 isReadOnlyForSaleSperson = true;
				    }
				 }
			
			model.addAttribute("isReadOnlyForSaleSperson", isReadOnlyForSaleSperson);
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
