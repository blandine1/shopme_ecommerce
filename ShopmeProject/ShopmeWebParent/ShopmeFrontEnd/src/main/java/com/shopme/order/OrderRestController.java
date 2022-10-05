package com.shopme.order;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.OrderNotFoundException;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.customer.CustomerService;

@RestController
public class OrderRestController {
	
	@Autowired private OrderService orderService;
	@Autowired private CustomerService customerService;
	
	@PostMapping("/orders/return")
	public ResponseEntity<?> handleReturnRequest(@RequestBody OrderReturnedRequest returnedRequest, HttpServletRequest request){
		
		
		
		System.out.println("order ID: " +returnedRequest.getOrderId());
		System.out.println("Reason: " +returnedRequest.getReason());
		System.out.println("Note: " +returnedRequest.getNote());
		
		Customer customer = null;
		try {
		 customer = getAuthenticatedCustomer(request);
		} catch (CustomerNotFoundException e) {
			return new ResponseEntity<>("Authentication required", HttpStatus.BAD_REQUEST);
		}
		
		try {
			orderService.setOrderReturnedRequested(returnedRequest, customer);
		} catch (OrderNotFoundException ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(new OrderReturnedResponse(returnedRequest.getOrderId()), HttpStatus.OK);
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if(email == null) {
			throw new CustomerNotFoundException("no authenticated customer");
		}
		
		return customerService.getCustomerByEmail(email);
	}

}
