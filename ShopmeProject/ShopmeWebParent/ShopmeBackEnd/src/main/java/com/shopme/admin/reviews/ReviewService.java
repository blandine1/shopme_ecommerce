package com.shopme.admin.reviews;

import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.product.ProductRepository;
import com.shopme.common.entity.Review;
import com.shopme.common.exception.ReviewNotFoundException;

@Service
@Transactional
public class ReviewService {
	public static final int REVIEWS_PER_PAGE = 5;
	
	@Autowired private ReviewsRepository reviewsRepository;
	@Autowired private ProductRepository productRepository;
	
	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, REVIEWS_PER_PAGE, reviewsRepository);
	}
	
	public Review get(Integer id) throws ReviewNotFoundException {
		try {
			return reviewsRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ReviewNotFoundException("could not find any reviews with ID " + id);
		}
	}
	
	public void save(Review reviewInForm) {
		Review reviewInDB = reviewsRepository.findById(reviewInForm.getId()).get();
		reviewInDB.setHeadline(reviewInForm.getHeadline());
		reviewInDB.setComment(reviewInForm.getComment());
		
		reviewsRepository.save(reviewInDB);
		productRepository.updateReviewCountAndAverageRating(reviewInDB.getProduct().getId());
	}
	
	public void delete(Integer id) throws ReviewNotFoundException {
		if(!reviewsRepository.existsById(id)) {
			throw new ReviewNotFoundException("coulmd not find any review with ID " + id);
		}
		
		reviewsRepository.deleteById(id);
	}
}
