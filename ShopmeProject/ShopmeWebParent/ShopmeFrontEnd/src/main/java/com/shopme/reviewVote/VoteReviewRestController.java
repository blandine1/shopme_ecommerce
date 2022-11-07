package com.shopme.reviewVote;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.ControllerHelper;
import com.shopme.common.entity.Customer;

@RestController
public class VoteReviewRestController {
	
	@Autowired private ReviewVoteService reviewVoteService;
	@Autowired private ControllerHelper controllerHelper;
	
	@PostMapping("/vote_review/{id}/{type}")
	public VoteResult voteReview(@PathVariable(name = "id") Integer reviewId, @PathVariable(name = "type") String type,
			                       HttpServletRequest request) {
		
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		if(customer == null) {
			return VoteResult.fail("you must login to vote this review");
		}
		
		VoteType voteType = VoteType.valueOf(type.toUpperCase());
		
		return reviewVoteService.doVote(reviewId, customer, voteType);
		
	}

}
