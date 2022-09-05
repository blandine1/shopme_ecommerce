package com.shopme.admin.order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.country.CountryRepository;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.setting.Setting;

@Controller
public class OrderController {
	
	private String defaultRedirectUrl = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
	
	@Autowired private OrderService orderService;
	@Autowired private SettingService settingService;
	
	@GetMapping("/orders")
	public String listFirstPage() {
		return defaultRedirectUrl;
	}
	
	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listOrders", moduleUrl = "/orders")PagingAndSortingHelper helper,
			                 @PathVariable(name = "pageNum") int pageNum,
			                 HttpServletRequest request ) {
		
		orderService.listByPage(pageNum, helper);
		loadCurrencySetting(request);
		
		return "orders/orders";
		
	}
	
	
	@GetMapping("/orders/detail/{id}")
	public String viewDetails(@PathVariable("id")Integer id, Model model, HttpServletRequest request, RedirectAttributes attributes) {
		
		try {
			Order order = orderService.get(id);
			loadCurrencySetting(request);
			model.addAttribute("order", order);
			
			return "orders/order_details_modal";
		} catch (OrderNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectUrl;
		}
	}
	
	@GetMapping("/orders/delete/{id}")
	public String deleteOrde(@PathVariable("id") Integer id, RedirectAttributes attributes) {
		try {
			orderService.deleteOrder(id);
			attributes.addFlashAttribute("message", "the Order with Id "+ id +" has been deleted");
		} catch (OrderNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		
		return defaultRedirectUrl;
	}
	
	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id,HttpServletRequest request,Model model, RedirectAttributes attributes) {
		try {
			Order order = orderService.get(id);
			List<Country> listCountries = orderService.listAllCountries();
			
			model.addAttribute("order", order);
			model.addAttribute("pageTitle", "Editing Order ID "+order.getId());
			model.addAttribute("listCountries", listCountries);
			
			return "orders/order_form";
		} catch (OrderNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		
		return defaultRedirectUrl;
	}

	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> currencySetting = settingService.getCurrencySetting();
		
		for (Setting setting : currencySetting) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}
		
	}
	

}
