package com.shopme.reviewVote;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.ReviewVote;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ReviewVoteRepositoryTest {
	
	@Autowired private ReviewVoteRepository reviewVoteRepository;
	
	@Test
	public void testSaveVote() {
		Integer customerId = 5;
		Integer reviewId = 10;
		
		ReviewVote vote = new ReviewVote();
		vote.setCustomer(new Customer(customerId));
		vote.setReview(new Review(reviewId));
		vote.voteUp();
		
		ReviewVote savedVote = reviewVoteRepository.save(vote);
		assertThat(savedVote.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testFindByReviewAndCustomer() {
		Integer customerId = 5;
		Integer reviewId = 10;
		
		ReviewVote vote = reviewVoteRepository.findByReviewAndCustomer(reviewId, customerId);
		assertThat(vote.getId()).isNotNull();
		
		System.out.println("vote : " + vote);
	}
	
	@Test
	public void testFindByProductAndCustomer() {
		Integer customerId = 5;
		Integer productId = 5;
		
		List<ReviewVote> listVotes = reviewVoteRepository.findByProductAndCustomer(productId, customerId);
		assertThat(listVotes.size()).isGreaterThan(0);
		
		listVotes.forEach(System.out :: println);
	}

}
