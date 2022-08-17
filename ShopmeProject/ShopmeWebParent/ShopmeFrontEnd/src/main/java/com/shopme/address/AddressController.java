package com.shopme.address;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;

@Controller
public class AddressController {
	
	@Autowired private AddressService addressService;
	
	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/address_book")
	public String listAddressBook(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		List<Address> listAddress= addressService.listAddressBOok(customer);
		
		
		boolean isPrimaryAddressAsDefault = true;
		for(Address address: listAddress) {
			if(address.isDefaultForShipping()) {
				isPrimaryAddressAsDefault=false;
				break;
			}
		}
		
		model.addAttribute("listAddress", listAddress);
		model.addAttribute("customer", customer);
		model.addAttribute("isPrimaryAddressAsDefault", isPrimaryAddressAsDefault);
		return "address_book/addresses";
	}
	
	@GetMapping("/address_book/new")
	public String newAddress(Model model) {
		List<Country> listCountries= customerService.findAll();
		
		
		model.addAttribute("address", new Address());
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "creating new customer Address");
		
		return "address_book/addresse_form";
	}
	
	@PostMapping("/address_book/save")
	public String saveAddress(Address address, RedirectAttributes attributes, HttpServletRequest request) {
		
		Customer customer = getAuthenticatedCustomer(request);
		address.setCustomer(customer);
		addressService.save(address);
		
		attributes.addFlashAttribute("message", "the address has been saved successfully");
		
		return "redirect:/address_book";
	}
	
	@GetMapping("/address_book/edit/{id}")
	public String editAddress(@PathVariable("id")Integer addressId, Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
        List<Country> listCountries= customerService.findAll();
		
		Address address = addressService.get(addressId, customer.getId());
		
		model.addAttribute("address", address);
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Editing address Id : "+addressId);
		
		return "address_book/addresse_form";
	}
	
	@GetMapping("/address_book/delete/{id}")
	public String deleteAddress(@PathVariable("id") Integer addressId, HttpServletRequest request, RedirectAttributes attributes) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.deletAdress(addressId, customer.getId());
		
		
		attributes.addFlashAttribute("message", "The address ID "+ addressId+ " has been deleted");
		
		return "redirect:/address_book";
	}
	
	@GetMapping("/address_book/default/{id}")
	public String setDefaultAddress(@PathVariable("id") Integer addressId, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.setDefaultAddress(addressId, customer.getId());
		
		String redirectOption = request.getParameter("redirect");
		String redirectUrl = "redirect:/address_book";
		
	    if("cart".equals(redirectOption)) {
			redirectUrl = "redirect:/cart";
		}
		
		return redirectUrl;
	}
	
	//on a plus besoi de verifier si le customer exuiste vraiment puisqu'avant de cliquer ici qu'il est deja logué
		private Customer getAuthenticatedCustomer(HttpServletRequest request) {
			String email = Utility.getEmailOfAuthenticatedCustomer(request);
			return customerService.getCustomerByEmail(email);
		}

}
