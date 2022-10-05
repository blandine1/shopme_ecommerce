package com.shopme.admin.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.order.Order;

public interface OrderRepository  extends SearchRepository<Order, Integer>{
	
	@Query("SELECT o FROM Order o WHERE CONCAT('#', o.id) like %?1% or CONCAT(o.firstName, ' ', o.lastName) like %?1% or"
			+ " o.firstName like %?1% or o.lastName like %?1% or "
			+ " o.phoneNumber like %?1% or o.addressLine1 like %?1% or "
			+ " o.addressLine2 like %?1% or o.postalCode like %?1% or "
			+ " o.city like %?1% or o.state like %?1% or "
			+ " o.country like %?1% or o.paymentMethod like %?1% or "
			+ " o.status like %?1% or o.customer.firstName like %?1% or "
			+ " o.customer.lastName like %?1%")
	public Page<Order> findAll(String keyword, Pageable pageable);
	
	public Long countById(Integer id);
}
