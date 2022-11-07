package com.shopme.admin.shippingrate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

@Controller
public class ShippingRateController {
	
	private String defaultRedirectUrl = "redirect:/shipping_rates/page/1?sortField=country&sortDir=asc";
	
	@Autowired private ShippingRateSrvice rateSrvice;
	
	@GetMapping("/shipping_rates")
	public String listFirstPage() {
		return defaultRedirectUrl;
	}

	@GetMapping("/shipping_rates/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "shippingRates",
	                          moduleUrl="/shipping_rates") PagingAndSortingHelper helper,
			                  @PathVariable(name = "pageNum") int pageNum) {
		rateSrvice.listByPage(pageNum, helper);
		return "/shipping_rates/shipping_rates";
		
	}
	
	@GetMapping("/shipping_rates/new")
	public String newRate(Model model) {
	     List<Country> listCountries = rateSrvice.listCountry();
	     
	     model.addAttribute("rate", new ShippingRate());
	     model.addAttribute("listCountries", listCountries);
	     model.addAttribute("pageTItle", "new rate");
	     
	     return "/shipping_rates/shipping_rate_form";
	}
	
	@PostMapping("/shipping_rates/save")
	public String saveRate(Model model, ShippingRate rate, RedirectAttributes attributes) {
		try {
			rateSrvice.save(rate);
			attributes.addFlashAttribute("message", "the shipping rate has been saved successfully");
		} catch (ShippingRateNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectUrl;
	}
	
	@GetMapping("/shipping_rates/edit/{id}")
	public String editRate(@PathVariable("id")Integer id, RedirectAttributes attributes, Model model) {
		try {
			ShippingRate rate = rateSrvice.get(id);
			 List<Country> listCountries = rateSrvice.listCountry();
		     
		     model.addAttribute("rate", rate);
		     model.addAttribute("listCountries", listCountries);
		     model.addAttribute("pageTitle", "editing rate ID : "+id);
		     
		     return "/shipping_rates/shipping_rate_form";
		} catch (ShippingRateNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			 return defaultRedirectUrl;
		}
	}
	
	@GetMapping("/shipping_rates/cod/{id}/enabled/{supported}")
	public String updateCODRate(@PathVariable("id")Integer id,@PathVariable("supported")Boolean supported, RedirectAttributes attributes, Model model) {
		try {
			rateSrvice.updateCODSupport(id, supported);
			
			attributes.addFlashAttribute("message", "COD support for shippingRate id : "+id+" has been "+ supported);
		} catch (ShippingRateNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		 return defaultRedirectUrl;
	}
	
	@GetMapping("/shipping_rates/delete/{id}")
	public String deleteRate(@PathVariable("id") Integer id, RedirectAttributes attributes,Model model) {
		try {
			rateSrvice.deleteShippingRate(id);
			attributes.addFlashAttribute("message", "The ShippingRate id : "+id+" has been deleted");
		} catch (Exception e) {
			attributes.addFlashAttribute("meaasge", e.getMessage());
		}
		return defaultRedirectUrl;
	}
	
	
	
	
	
}
