package com.shopme.order;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.ControllerHelper;
import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.product.Product;
import com.shopme.customer.CustomerService;
import com.shopme.review.ReviewService;

@Controller
public class OrderController {

	@Autowired ControllerHelper controllerHelper;
	@Autowired private OrderService orderService;
	@Autowired private ReviewService reviewService;
	
	@GetMapping("/orders")
	public String listFirstPage(Model model, HttpServletRequest request) {
		return listOrdersByPage(model, request, 1, "orderTime", "desc", null);
	}
	
	@GetMapping("/orders/page/{pageNum}")
	public String listOrdersByPage(Model model, HttpServletRequest request, 
			 @PathVariable(name = "pageNum") int pageNum, String sortField, String sortDir, String orderKeyword) {
		
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		Page<Order> page = orderService.listForCustomerByPage(customer, pageNum, sortField, sortDir, orderKeyword);
		List<Order> listOrders = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("orderKeyword", orderKeyword);
		model.addAttribute("moduleUrl", "/orders");
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc":"asc");
		
		long startCount = (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		return "orders/orders_customer";
	}
	
	@GetMapping("/orders/details/{id}")
	public String veiwOrderDetails(Model model, @PathVariable(name = "id") Integer id, HttpServletRequest request) {
		
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		Order order = orderService.getOrder(id, customer);
		
		setProductReviewableStatus(customer, order);
		
		model.addAttribute("order", order);
		
		return "orders/order_details_modal";
	}
	
	private void setProductReviewableStatus(Customer customer, Order order) {
		 Iterator<OrderDetail> iterator = order.getOrderDetails().iterator();
		 
		 while(iterator.hasNext()) {
			 OrderDetail orderDetail = iterator.next();
			 Product product = orderDetail.getProduct();
			 Integer productID = product.getId();
			 
			 //par defaut on dit le client a deja laisser une note
			 boolean didCustomerReviewProduct = reviewService.didCustomerReviewProduct(customer, productID);
			 product.setReviewedByCustomer(didCustomerReviewProduct);
			 
			 //mais si la note n'a pas encoe ete laissée alors on lui permet de le faire
			 if(!didCustomerReviewProduct) {
				 boolean canCustomerReviewProduct = reviewService.canCustomerReviewProduct(customer, productID);
				 product.setCustomerCanReview(canCustomerReviewProduct);
			 }
			 
		 }
	}

	//on a plus besoi de verifier si le customer exuiste vraiment puisqu'avant de cliquer ici qu'il est deja logué
		

}
