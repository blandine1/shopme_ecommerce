package com.shopme.admin.order;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.OrderTrack;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.setting.Setting;
import com.shopme.common.exception.OrderNotFoundException;

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
			                 HttpServletRequest request, @AuthenticationPrincipal ShopmeUserDetails loggedUser ) {
		
		orderService.listByPage(pageNum, helper);
		loadCurrencySetting(request);
		
		if(!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper")) {
			return "orders/orders_shipper";
		}
		
		return "orders/orders";
		
	}
	
	
	@GetMapping("/orders/detail/{id}")
	public String viewDetails(@PathVariable("id")Integer id, Model model, HttpServletRequest request, 
			RedirectAttributes attributes, @AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		
		try {
			Order order = orderService.get(id);
			loadCurrencySetting(request);
			
			boolean isVisibleForAdminAndSalesperson = false;
			
			if(loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson")) {
				isVisibleForAdminAndSalesperson = true;
			}
			
			model.addAttribute("isVisibleForAdminAndSalesperson", isVisibleForAdminAndSalesperson);
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
	
	@PostMapping("/orders/save")
	public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes attributes) {
		
		String countryName = request.getParameter("countryName");
		order.setCountry(countryName);
		
		
		updateProductDetails(order, request);
		updateOrderTrack(order, request);
		
		orderService.save(order);
		
		
		attributes.addFlashAttribute("message", "The order Id :" + order.getId() + " has been updated successfully");
		
		return defaultRedirectUrl;
	}
	
	
	private void updateOrderTrack(Order order, HttpServletRequest request) {

		String[] trackIds = request.getParameterValues("trackId");
		String[] trackDates = request.getParameterValues("trackDate");
		String[] trackStatuses = request.getParameterValues("trackStatus");
		String[] trackNotes = request.getParameterValues("trackNotes");
		
		List<OrderTrack> orderTracks = order.getOrderTracks();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		for(int i=0; i< trackIds.length; i++) {
			OrderTrack trackRecord = new OrderTrack();
			Integer trackId = Integer.parseInt(trackIds[i]);
			if(trackId > 0) {
				trackRecord.setId(trackId);
			}
			
			trackRecord.setOrder(order);
			trackRecord.setStatus(OrderStatus.valueOf(trackStatuses[i]));
			trackRecord.setNotes(trackNotes[i]);
			try {
				trackRecord.setUpdatedTime(dateFormat.parse(trackDates[i]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			orderTracks.add(trackRecord);
			
		}
	}

	private void updateProductDetails(Order order, HttpServletRequest request) {
		String[] detailIds = request.getParameterValues("detailId");
		String[] productIds = request.getParameterValues("productId");
		String[] productCosts = request.getParameterValues("productDetailCost");
		String[] quantities = request.getParameterValues("quantity");
		String[] productPrices = request.getParameterValues("productPrice");
		String[] productSubtotals = request.getParameterValues("productSubtotal");
		String[] productShipCost = request.getParameterValues("productShipCost");
		
	
		Set<OrderDetail> orderDetails = order.getOrderDetails();
		
		for(int i=0; i < detailIds.length; i++) {
			System.out.println("details Id : " + detailIds[i]);
			System.out.println("\t product Id : " + productIds[i]);
			System.out.println("\t product cost : " + productCosts[i]);
			System.out.println("\t quantities : " + quantities[i]);
			System.out.println("\t product price : " + productPrices[i]);
			System.out.println("\t product subtotal : " + productSubtotals[i]);
			System.out.println("\t product ship cost : " + productShipCost[i]);
			
			
			
			System.out.println("the firstm name of the order man" +order.getCustomer());
			//System.out.println("the last name of the order man" +order.getCustomer().getFirstName());
			
			
			  OrderDetail orderDetail = new OrderDetail(); Integer detailId =
			  Integer.parseInt(detailIds[i]); if(detailId > 0) {
			  orderDetail.setId(detailId); }
			 
			
			
			  orderDetail.setOrder(order); 
			  orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
			  orderDetail.setProductCost(Float.parseFloat(productCosts[i].replace(",", "")));
			  orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i].replace(",", "")));
			  orderDetail.setQuantity(Integer.parseInt(quantities[i]));
			  orderDetail.setUnitPrice(Float.parseFloat(productPrices[i].replace(",", "")));
			  orderDetail.setShippingCost(Float.parseFloat(productShipCost[i].replace(",", "")));
			  
			  
			  orderDetails.add(orderDetail);
			 
		}
		
	}

	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> currencySetting = settingService.getCurrencySetting();
		
		for (Setting setting : currencySetting) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}
		
	}

}
