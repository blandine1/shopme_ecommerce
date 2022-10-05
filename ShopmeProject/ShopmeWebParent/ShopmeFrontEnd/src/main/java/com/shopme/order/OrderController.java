package com.shopme.order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.customer.CustomerService;

@Controller
public class OrderController {

	@Autowired CustomerService customerService;
	@Autowired private OrderService orderService;
	
	@GetMapping("/orders")
	public String listFirstPage(Model model, HttpServletRequest request) {
		return listOrdersByPage(model, request, 1, "orderTime", "desc", null);
	}
	
	@GetMapping("/orders/page/{pageNum}")
	public String listOrdersByPage(Model model, HttpServletRequest request, 
			 @PathVariable(name = "pageNum") int pageNum, String sortField, String sortDir, String orderKeyword) {
		
		Customer customer = getAuthenticatedCustomer(request);
		
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
		
		Customer customer = getAuthenticatedCustomer(request);
		
		Order order = orderService.getOrder(id, customer);
		
		model.addAttribute("order", order);
		
		return "orders/order_details_modal";
	}
	
	 //on a plus besoi de verifier si le customer exuiste vraiment puisqu'avant de cliquer ici qu'il est deja logué
		private Customer getAuthenticatedCustomer(HttpServletRequest request) {
			String email = Utility.getEmailOfAuthenticatedCustomer(request);
			return customerService.getCustomerByEmail(email);
		}


}
