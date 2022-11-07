package com.shopme.admin.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@Controller
public class CustomerController {

	@Autowired
	private CustomerService  customerService;
	
	@GetMapping("/customers")
	public String listFirstPage(Model model) {
		return listByPage(model, 1, "firstName", "asc", null);
	}
	
	@GetMapping("/customers/page/{pageNum}")
	private String listByPage(Model model, 
			                  @PathVariable(name = "pageNum") int pageNum, 
			                  @Param("sortField") String sortField,
			                  @Param("sortDir") String sortDir, 
			                  @Param("keyword") String keyword) {
       
		Page<Customer> page = customerService.listByPage(pageNum, keyword, sortField, sortDir);
		List<Customer> listCustomers = page.getContent();
		
		long startCount = (pageNum - 1) * CustomerService.CUSTOMER_PER_PAGE;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + CustomerService.CUSTOMER_PER_PAGE -1;
		
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listCustomers", listCustomers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("endCount", endCount);
		model.addAttribute("moduleUrl", "/customers");
		
		return "customers/customers";
	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateCustomerStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, Model model,
			RedirectAttributes attributes) {
		customerService.UpdateCustomerEnabledStatus(id, enabled);
		
		String status = enabled ? "enabled" : "desabled";
		String message = "The customer with ID "+ id +"has been "+ status;
		attributes.addFlashAttribute("message", message);
		
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/detail/{id}")
	public String viewCustomer(@PathVariable("id")Integer id,Model model, RedirectAttributes attributes) {
		try {
			Customer customer = customerService.getSingle(id);
			model.addAttribute("customer", customer);
			
			return "customers/customer_detail_modal";
		} catch (CustomerNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			
			return "redirect:/customers";
		}
	}
	
	@GetMapping("/customers/edit/{id}")
	public String editCustomer(@PathVariable("id")Integer id,Model model, RedirectAttributes attributes) {
		try {
			Customer customer = customerService.getSingle(id);
			List<Country> listAllCountries = customerService.listAllCountries();
			
			 model.addAttribute("customer", customer);
			 model.addAttribute("listAllCountries", listAllCountries);
			 model.addAttribute("pageTitle", "editing custiomer "+ customer.getFirstName());
			
			 return "customers/customer_form";
		} catch (CustomerNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			
			return "redirect:/customers";
		}
	}
	
	@PostMapping("/customers/save")
	public String saveCustomer(Customer customer, Model model, RedirectAttributes attributes) {
		Customer savedCustomer = customerService.saveCustomer(customer);
		attributes.addFlashAttribute("message", "the customer "+savedCustomer.getFirstName()+ " is saved successfully");
		
		return "redirect:/customers";
	}

	@GetMapping("/customers/delete/{id}")
	public String deleteCustomer(@PathVariable("id") Integer id, Model  model, RedirectAttributes attributes) {
		try {
			customerService.deleteCustomer(id);
			attributes.addFlashAttribute("message", "The customer id "+id + " has been successfully deleted");
			
			return "customers/customers";
		} catch (CustomerNotFoundException e) {
          		attributes.addFlashAttribute("message", e.getMessage());  
		}
		return "redirect:/customers";
	}
}
