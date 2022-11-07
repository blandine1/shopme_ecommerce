package com.shopme.admin.reviews;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ReviewsRepositoryTests {
	
	@Autowired private ReviewsRepository reviewsRepository;
	
	@Test
	public void testCreateReviews() {
		Integer productId = 70;
		Product product = new Product(productId);
		
		Integer customerId = 40;
		Customer customer = new Customer(customerId);
		
		Review reviews = new Review();
		reviews.setProduct(product);
		reviews.setCustomer(customer);
		reviews.setHeadline("Perfectly for graphisme..");
		reviews.setComment("Need one please.. deliver tomorrow");
		reviews.setReviewTime(new Date());
		reviews.setRating(5);
		
		Review saved = reviewsRepository.save(reviews);
		
		assertThat(saved.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testLIstReviews() {
		List<Review> listReviews = reviewsRepository.findAll();
		
		listReviews.forEach(r ->{
			System.out.println(r);
		});
	}
	
	@Test
	public void testGetReviews() {
		Integer id = 1;
		Review reviews = reviewsRepository.findById(id).get();
		
		System.out.println(reviews);
		
		assertThat(reviews).isNotNull();
	}
	
	@Test
	public void testUpdateReviews() {
		Integer id = 1;
		Review reviews = reviewsRepository.findById(id).get();
		
		reviews.setHeadline("Exactly what i wants. Thank you");
		
		reviewsRepository.save(reviews);
	}
	
	@Test
	public void testDeleteReviews() {
		Integer id = 6;
		
		reviewsRepository.deleteById(id);
	}
	
	@Test
	public void testDeleteRevie() {
		
	}

}
