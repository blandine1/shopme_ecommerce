package com.shopme.product;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.ControllerHelper;
import com.shopme.Utility;
import com.shopme.Category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.common.exception.ProductNotFountException;
import com.shopme.customer.CustomerService;
import com.shopme.review.ReviewService;
import com.shopme.reviewVote.ReviewVoteService;

@Controller
public class ProductController {

	@Autowired private CategoryService catService;
	@Autowired private ProductService productService;
	@Autowired private ReviewService reviewService;
	@Autowired private ControllerHelper controllerHelper;
	@Autowired private ReviewVoteService reviewVoteService;
	
	@GetMapping("/c/{category_alias}")
	public String viewcategoryByFirstPage(@PathVariable("category_alias")String alias, Model model) {
		return viewcategoryByPage(alias,1, model);
	}
	
	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewcategoryByPage(@PathVariable("category_alias")String alias,@PathVariable("pageNum") int pageNum ,Model model) {
		 try {
		       Category category = catService.getCategory(alias);
		      
		 
		       List<Category> listCategoryParents = catService.getCategoryParents(category);
		       
		       Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
		       List<Product> listProducts = pageProducts.getContent();
		 
		       long startCount = (pageNum - 1) * ProductService.PRODUCT_PER_PAGE + 1;
			   long endCount = startCount + ProductService.PRODUCT_PER_PAGE + 1;
			   if(endCount > pageProducts.getTotalElements()) {
			    	endCount = pageProducts.getTotalElements();
			    }
			
		
			    model.addAttribute("listProducts", listProducts);
			    model.addAttribute("currentPage", pageNum);
			    model.addAttribute("totalPages", pageProducts.getTotalPages());
			    model.addAttribute("startCount", startCount);
			    model.addAttribute("endCount", endCount);
			    model.addAttribute("totalItems", pageProducts.getTotalElements());
		        model.addAttribute("pageTitle", category.getName());
		        model.addAttribute("listCategoryParents", listCategoryParents);
		        model.addAttribute("category", category);
		 
		        return "product/products_by_category";
	    } catch(CategoryNotFoundException e) {
	    	return "error/404";
	    }
	}
	
	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable("product_alias")String product_alias, Model model, HttpServletRequest request) {
		
		try {
			Product product = productService.findProductByAlias(product_alias);
			List<Category> listCategoryParents = catService.getCategoryParents(product.getCategory());
			Page<Review> listReviews = reviewService.list3MostRecentReviewByProduct(product);
			
			Customer customer = controllerHelper.getAuthenticatedCustomer(request);
			
			if(customer != null) {
				boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
				
				reviewVoteService.markReviewVotedForProductByCustomer(listReviews.getContent(), product.getId(), customer.getId());
				
				if(customerReviewed) {
					//deja laissé une note
					model.addAttribute("customerReviewed", customerReviewed);
				}else {
					//il peut laisser une note
					boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
					model.addAttribute("customerCanReview", customerCanReview);
				}
			}
			
			model.addAttribute("product", product);
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("listReviews", listReviews);
			model.addAttribute("pageTitle", product.getShortName());
			 
			return "product/product_detail";
		} catch (ProductNotFountException e) {
			return "error/404";
		}
	}
	
	@GetMapping("/search")
	public String searchFirstPage(@Param("keyword") String keyword, Model model) {
		return searchByPage(keyword, 1, model);
	}
	
	@GetMapping("/search/page/{pageNum}")
	public String searchByPage(@Param("keyword") String keyword, @PathVariable("pageNum") int pageNum
			,Model model) {
		Page<Product> pageProduct = productService.search(keyword, pageNum);
		List<Product> listResult = pageProduct.getContent();
		
		 long startCount = (pageNum - 1) * ProductService.SEARCH_RESULT_PER_PAGE + 1;
		   long endCount = startCount + ProductService.SEARCH_RESULT_PER_PAGE + 1;
		   if(endCount > pageProduct.getTotalElements()) {
		    	endCount = pageProduct.getTotalElements();
		    }
		
	
		    model.addAttribute("listResult", listResult);
		    model.addAttribute("currentPage", pageNum);
		    model.addAttribute("totalPages", pageProduct.getTotalPages());
		    model.addAttribute("startCount", startCount);
		    model.addAttribute("endCount", endCount);
		    model.addAttribute("totalItems", pageProduct.getTotalElements());
	        model.addAttribute("pageTitle", keyword + " - search result");
		    model.addAttribute("keyword", keyword);
		return "product/search_result";
	}
	
	//on a plus besoi de verifier si le customer exuiste vraiment puisqu'avant de cliquer ici qu'il est deja logué
	
}
