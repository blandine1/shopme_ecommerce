package com.shopme.review;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.ControllerHelper;
import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFountException;
import com.shopme.common.exception.ReviewNotFoundException;
import com.shopme.customer.CustomerService;
import com.shopme.product.ProductService;
import com.shopme.reviewVote.ReviewVoteService;

@Controller
public class ReviewController {

	private String defaultRedirectURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";
	
	@Autowired private ReviewService reviewService;
	@Autowired private ControllerHelper controllerHelper;
	@Autowired private ProductService productService;
	@Autowired private ReviewVoteService reviewVoteService;
	
	@GetMapping("/reviews")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}
	
	@GetMapping("/reviews/page/{pageNum}")
	public String listReviewsByCustomer(Model model, HttpServletRequest request, @PathVariable(name = "pageNum") int pageNum,
			                                 String keyword, String sortField, String sortDir) {
		
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		Page<Review> page = reviewService.ListByCustomerByPage(customer, keyword, pageNum, sortField, sortDir);
		List<Review> listReviews = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("moduleUrl", "/reviews");
		
		model.addAttribute("listReviews", listReviews);
		long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
		model.addAttribute("startcount", startCount);
		
		long endCount = pageNum + ReviewService.REVIEWS_PER_PAGE - 1;
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		return "reviews/reviews_customer";
		
	}
	
	@GetMapping("/reviews/detail/{id}")
	public String viewReview(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes, HttpServletRequest request) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		try {
			Review review = reviewService.getByCustomerAndId(customer, id);
			model.addAttribute("review", review);
			
			return "reviews/review_detail_modal";
		} catch (ReviewNotFoundException e) {
	        attributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;
		}
	}
	
	//on a plus besoi de verifier si le customer exuiste vraiment puisqu'avant de cliquer ici qu'il est deja logué
	
	
	@GetMapping("/ratings/{productAlias}/page/{pageNum}")
	public String listByProductByPage(Model model, @PathVariable(name = "productAlias") String productAlias,
			                                       @PathVariable(name = "pageNum") int pageNum, String sortField, String sortDir,
			                                       HttpServletRequest request) {
		
		Product product = null;
		
		try {
			product = productService.findProductByAlias(productAlias);
			
		} catch (ProductNotFountException e) {
			return "error/404";
		}
		
		Page<Review> page = reviewService.listByProduct(product, pageNum, sortField, sortDir);
	    List<Review> listReviews = page.getContent();
	    
	    Customer customer = controllerHelper.getAuthenticatedCustomer(request);
	    if(customer != null) {
	    	reviewVoteService.markReviewVotedForProductByCustomer(listReviews, product.getId(), customer.getId());
	    } 
	    
	    model.addAttribute("totalPages", page.getTotalPages());
	    model.addAttribute("totalItems", page.getTotalElements());
	    model.addAttribute("currentPage", pageNum);
	    model.addAttribute("sortField",sortField);
	    model.addAttribute("sortDir",sortDir);
	    model.addAttribute("reverseSortDir",sortDir.equals("asc") ? "desc" : "asc");
		
	    model.addAttribute("listReviews",listReviews);
	    model.addAttribute("product",product);
	    
	    long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
	    model.addAttribute("startCount", startCount);
	    
	    long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
	    if(endCount > page.getTotalElements()) {
	    	endCount = page.getTotalElements();
	    }
	    
	    model.addAttribute("endCount", endCount);
	    model.addAttribute("pageTitle", "Review for "+ product.getShortName());
	    
	    return "reviews/reviews_product";
	}
	
	@GetMapping("/ratings/{productAlias}")
	public String listByProductFirstPage(@PathVariable(name = "productAlias") String productAlias, Model model, HttpServletRequest request) {
		return listByProductByPage(model, productAlias, 1, "reviewTime", "desc", request);
	}
	
	@GetMapping("/write_review/product/{productId}")
	public String showReviewForm(@PathVariable("productId") Integer productId, Model model, HttpServletRequest request) {
		
		Review review = new Review();
		Product product = null;
		try {
			 product = productService.get(productId);
		} catch (ProductNotFountException e) {
			return "error/404";
		}
		
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
		
		if(customerReviewed) {
			//deja laissé une note
			model.addAttribute("customerReviewed", customerReviewed);
		}else {
			//il peut laisser une note
			boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
			
			if(customerCanReview) {
				model.addAttribute("customerCanReview", customerCanReview);
			}else {
				model.addAttribute("NoReviewPermission", true);
			}
		}
		
		model.addAttribute("product", product);
		model.addAttribute("review", review);
		
		return "reviews/review_form";
		
	}
	
	@PostMapping("/post_review")
	public String saveReview(Model model, Review review, Integer productId, HttpServletRequest request ) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		Product product = null;
		try {
			 product = productService.get(productId);
		} catch (ProductNotFountException e) {
			return "error/404";
		}
		
		review.setCustomer(customer);
		review.setProduct(product);
		
		Review saveReview = reviewService.saveReview(review);
		
		model.addAttribute("review", saveReview);
		
		return "reviews/review_done";
	}
}
