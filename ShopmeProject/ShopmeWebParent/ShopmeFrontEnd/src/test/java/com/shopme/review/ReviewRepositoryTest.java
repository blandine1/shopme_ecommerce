package com.shopme.review;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ReviewRepositoryTest {
	
	@Autowired private ReviewRepository reviewRepository;
	
	@Test
	public void testFindByCustomerNoKeyword() {
		Integer customerId = 39;
		Pageable pageable = PageRequest.of(1, 5);
		
		Page<Review> page = reviewRepository.findByCustomer(customerId, pageable);
		long totalElements = page.getTotalElements();
		
		assertThat(totalElements).isGreaterThan(1);
	}
	
	@Test
	public void testFindByCustomerIthKeyNote() {
		//ici en fait pour un utilisateur ront par exemple on veut lui retourner toutes ses nots et en fonction de tout cela il doit etre ca
		//capable de faire une recherche sur ses propres commentaires
		Integer customerId = 39;
		String keynote = "Order";
		
		Pageable pageable = PageRequest.of(1, 5);
		Page<Review> page = reviewRepository.findByCustomer(customerId, keynote, pageable);
		long totalElements = page.getTotalElements();
		System.out.println("printing total elements : "+ totalElements);
		
		assertThat(totalElements).isGreaterThan(0);
	}
	

	@Test
	public void testFindByCustomerAndId() {
		Integer customerId = 39;
		Integer reviewId = 4;
		
		Review review = reviewRepository.findByCustomerAndId(customerId, reviewId);
		System.out.println("Review :" + review);
		
		assertThat(review).isNotNull();
		
	}
	
	@Test
	public void testFindByProduct() {
		Product product = new Product(59);
		Pageable pageable= PageRequest.of(0, 3);
		
		Page<Review> page = reviewRepository.findByProduct(product, pageable);
		
		List<Review> content = page.getContent();
        content.forEach(System.out:: println);
        
        assertThat(page.getTotalElements()).isGreaterThan(1);
	}
	
	@Test
	public void testCountByCustomerAndProduct() {
		Integer customerId = 40;
		Integer productId = 59;
		
		Long count = reviewRepository.countByCustomerAndProduct(customerId, productId);
		System.out.println("cout :" + count);
		assertThat(count).isEqualTo(1);
	}
	
	@Test
	public void testUpdateVoteCount() {
		Integer reviewId = 4;
		reviewRepository.updateVoteCount(reviewId);
		
		Review review = reviewRepository.findById(reviewId).get();
		
		assertThat(review.getVotes()).isEqualTo(1);
		System.out.println(review);
		
	}
	
	
}
