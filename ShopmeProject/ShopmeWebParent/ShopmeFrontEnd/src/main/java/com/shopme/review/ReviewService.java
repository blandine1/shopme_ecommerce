package com.shopme.review;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ReviewNotFoundException;
import com.shopme.order.OrderDetailRepository;
import com.shopme.product.ProductRepository;

@Service
@Transactional
public class ReviewService {

	public static final int REVIEWS_PER_PAGE = 5;
	
	@Autowired private ReviewRepository reviewRepository;
	@Autowired private OrderDetailRepository orderDetailRepository;
	@Autowired private ProductRepository productRepository;
	
	public Page<Review> ListByCustomerByPage(Customer customer, String keyword, int pageNum, String sortField, String sortDir){
		
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);
		
		if(keyword != null) {
			return reviewRepository.findByCustomer(customer.getId(), keyword, pageable);
		}
		
		return reviewRepository.findByCustomer(customer.getId(), pageable);
	}
	
	
	public Review getByCustomerAndId(Customer customer, Integer reviewId) throws ReviewNotFoundException {
		Review review = reviewRepository.findByCustomerAndId(customer.getId(), reviewId);
		
		if(review == null)
			throw new ReviewNotFoundException("Customer does not have any reviews with ID : " +reviewId);
		return review;
	}
	
	//list en premier les 3 premier produit les plus voté
	public Page<Review> list3MostVoteReviewsByProduct(Product product){
		 Sort sort = Sort.by("votes").ascending();
		 Pageable pageable = PageRequest.of(0,  3, sort);
		 
		 return reviewRepository.findByProduct(product, pageable);
	}
	
	
	public Page<Review> list3MostRecentReviewByProduct(Product product){
		Sort sort = Sort.by("reviewTime").ascending();
		Pageable pageable = PageRequest.of(0, 3, sort);
		
		return reviewRepository.findByProduct(product, pageable);
		
	}
	
	public Page<Review> listByProduct(Product product, int pageNum, String sortField, String sortDir){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);
		
		return reviewRepository.findByProduct(product, pageable);
	}
	
	//le client a deja laisse une note a ce produit
	public boolean didCustomerReviewProduct(Customer customer, Integer productId) {
		Long count = reviewRepository.countByCustomerAndProduct(customer.getId(), productId);
		return count > 0;
	}
	
	//le client peut laisser une note
	public boolean canCustomerReviewProduct(Customer customer, Integer productId) {
		Long count = orderDetailRepository.countByProductAndCustomerAndOrderStatus(productId, customer.getId(), OrderStatus.DELIVERED);
		
		return count > 0;
	}
	
	public Review saveReview(Review review) {
		review.setReviewTime(new Date());
		Review savedReview = reviewRepository.save(review);
		
		Integer productId = savedReview.getProduct().getId();
		productRepository.updateReviewCountAndAverageRating(productId);
		
		return savedReview;
	}
}
