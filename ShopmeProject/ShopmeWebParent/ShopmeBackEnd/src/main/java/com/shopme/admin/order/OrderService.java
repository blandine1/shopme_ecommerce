package com.shopme.admin.order;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.country.CountryRepository;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.OrderTrack;
import com.shopme.common.exception.OrderNotFoundException;

@Service
@Transactional
public class OrderService {
	
	private static final int ORDERS_PER_PAGE = 10;
	
	@Autowired private OrderRepository orderRepository;
	@Autowired private CountryRepository countryRepository;
	
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
	
	//la luste des pays
		public List<Country> listAllCountries(){
			return countryRepository.findAllByOrderByNameAsc();
		}
	
	public void deleteOrder(Integer id) throws OrderNotFoundException {
	    Long countById = orderRepository.countById(id);
	    if(countById == null || countById == 0) {
	    	throw new OrderNotFoundException("no order is found with the given id "+ id);
	    }
	    
	    orderRepository.deleteById(id);
	}
 
	public void save(Order orderInForm) {
		Order orderInDB = orderRepository.findById(orderInForm.getId()).get();
		orderInForm.setOrderTime(orderInDB.getOrderTime());
		orderInForm.setCustomer(orderInDB.getCustomer());
		
		System.out.println("////////////////////// "+ orderInForm);
		orderRepository.save(orderInForm);
		
	}
	
	public void updateStatus(Integer orderId, String status) {
		Order orderInDb = orderRepository.findById(orderId).get();
		OrderStatus statusToUpdate = OrderStatus.valueOf(status);
		
		if(!orderInDb.hasStatus(statusToUpdate)) {
			List<OrderTrack> orderTracks = orderInDb.getOrderTracks();
			
			OrderTrack track = new OrderTrack();
			track.setOrder(orderInDb);
			track.setStatus(statusToUpdate);
			track.setUpdatedTime(new Date());
			track.setNotes(statusToUpdate.defaultDescription());
			
			orderTracks.add(track);
			
			orderInDb.setStatus(statusToUpdate);
			
			orderRepository.save(orderInDb);
		}
	}

}
