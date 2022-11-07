package com.shopme.admin.reviews;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.Review;
import com.shopme.common.exception.ReviewNotFoundException;

@Controller
public class ReviewController {
	
	private String defaultRedirectURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";
	
	@Autowired private ReviewService reviewService;
	
	@GetMapping("/reviews")
	public String listFirstPage() {
		return defaultRedirectURL;
	}
	
	@GetMapping("/reviews/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listReviews", moduleUrl = "/reviews") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {
		
		reviewService.listByPage(pageNum, helper);
		
		return "reviews/reviews";
	}
	
	@GetMapping("/reviews/detail/{id}")
	public String viewReview(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			Review review = reviewService.get(id);
			model.addAttribute(review);
			
			return "reviews/review_detail_modal";
		} catch (ReviewNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;
		}
		
	}
	
	@GetMapping("/reviews/edit/{id}")
	public String editReview(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			Review review = reviewService.get(id);
			
			model.addAttribute("review", review);
			model.addAttribute("pageTitle", String.format("Edit Review (ID: %d)", id));
			
			return "reviews/review_form";
		} catch (ReviewNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;
		}
	}
	
	@PostMapping("/reviews/save")
	public String saveReview(Review reviewInForm, RedirectAttributes attributes) {
		reviewService.save(reviewInForm);
		
		attributes.addFlashAttribute("message", "The review ID "+ reviewInForm.getId() + " has been updated successfully.");
		return defaultRedirectURL;
	}
	
	@GetMapping("/reviews/delete/{id}")
	public String deleteReview(@PathVariable("id")Integer id, RedirectAttributes attributes) {
		try {
			reviewService.delete(id);
			attributes.addFlashAttribute("message", "The review ID " + id + " has been deleted successfully");
		} catch (Exception e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		
		return defaultRedirectURL;
	}
	
	
	

}
