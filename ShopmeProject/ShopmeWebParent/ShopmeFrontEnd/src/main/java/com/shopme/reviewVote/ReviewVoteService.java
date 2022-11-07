package com.shopme.reviewVote;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.ReviewVote;
import com.shopme.review.ReviewRepository;

@Service
@Transactional
public class ReviewVoteService {
	
	@Autowired private ReviewRepository reviewRepository;
	@Autowired private ReviewVoteRepository reviewVoteRepository;
	
	public VoteResult undoVote(ReviewVote vote, Integer reviewId, VoteType voteType) {
		reviewVoteRepository.delete(vote);
		reviewRepository.updateVoteCount(reviewId);
		
		Integer voteCount = reviewRepository.getVoteCount(reviewId);
		
		return VoteResult.success("You have unvoted " + voteType + " that review", voteCount);
	}
	
	public VoteResult doVote(Integer reviewId, Customer customer, VoteType voteType) {
		Review review = null;
		
		try {
			review = reviewRepository.findById(reviewId).get();
		} catch (NoSuchElementException e) {
			return VoteResult.fail("The review ID " + reviewId + " no longer exists");
		}
		
		ReviewVote vote = reviewVoteRepository.findByReviewAndCustomer(reviewId, customer.getId());
		if(vote != null) {
			if(vote.isUpVoted() && voteType.equals(VoteType.UP) || vote.isDownVoted() && voteType.equals(VoteType.DOWN)) {
				return undoVote(vote, reviewId, voteType);
			}else if(vote.isUpVoted() && voteType.equals(VoteType.DOWN)){
				vote.voteDown();
			}else if(vote.isDownVoted() && voteType.equals(VoteType.UP)) {
				vote.voteUp();
			}
		}else {
			vote = new ReviewVote();
			vote.setCustomer(customer);
			vote.setReview(review);
			
			if(voteType.equals(VoteType.UP)) {
				vote.voteUp();
			}else {
				vote.voteDown();
			}
		}
		
		reviewVoteRepository.save(vote);
		reviewRepository.updateVoteCount(reviewId);
		
		Integer voteCount = reviewRepository.getVoteCount(reviewId);
		
		return VoteResult.success("You have successfully voted "+ voteType +" that review", voteCount);
	}
	
	public void markReviewVotedForProductByCustomer(List<Review> listReviews, Integer productId, Integer customerId) {
		
		List<ReviewVote> listVotes = reviewVoteRepository.findByProductAndCustomer(productId, customerId);
		
		for(ReviewVote vote : listVotes) {
			Review voteReview = vote.getReview();
			
			if(listReviews.contains(voteReview)) {
				int index = listReviews.indexOf(voteReview);
				Review review = listReviews.get(index);
				
				if(vote.isUpVoted()) {
					review.setUpvotedByCurrentCustomer(true);
				}else if(vote.isDownVoted()){
					review.setDownvotedByCurrentCustomer(true);
				}
			}
		}
	}

}
