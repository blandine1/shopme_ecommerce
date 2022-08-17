package com.shopme.admin.order;

import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.order.Order;

@Service
@Transactional
public class OrderService {
	
	private static final int ORDERS_PER_PAGE = 10;
	
	@Autowired private OrderRepository orderRepository;
	
	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		String sortField = helper.getSortField();
		String sortDir = helper.getSortDir();
		String keyword = helper.getKeyword();
		
		Sort sort= null;
		
		if("destination".equals(sortField)) {
			sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
		}else {
			sort = Sort.by(sortField);
		}
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of( pageNum - 1, ORDERS_PER_PAGE, sort);
		
		Page<Order> page = null;
		
		if(keyword != null) {
			page = orderRepository.findAll(keyword, pageable);
		}else {
			page = orderRepository.findAll(pageable);
		}
		
		helper.updateModelAttributes(pageNum, page);
	}
	
	public Order get(Integer id) throws OrderNotFoundException {
		try {
			return orderRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new OrderNotFoundException("could not find Order with ID "+ id);
		}
	}
	
	public void deleteOrder(Integer id) throws OrderNotFoundException {
	    Long countById = orderRepository.countById(id);
	    if(countById == null || countById == 0) {
	    	throw new OrderNotFoundException("no order is found with the given id "+ id);
	    }
	    
	    orderRepository.deleteById(id);
	}

}