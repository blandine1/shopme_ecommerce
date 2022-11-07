package com.shopme.shoppingcart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.ControllerHelper;
import com.shopme.Utility;
import com.shopme.address.AddressService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.customer.CustomerService;
import com.shopme.shipping.ShippingRateService;

@Controller
public class ShoppingCartController {
	
	
	@Autowired private ShoppingCartService cartService;
	@Autowired private ControllerHelper controllerHelper;
	@Autowired private ShippingRateService rateService;
	@Autowired private AddressService addressService;
	
	@GetMapping("/cart")
	public String viewCart(Model model, HttpServletRequest request) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		List<CartItem> cartItems = cartService.listCartItem(customer);
		
		float estimatedTotal = 0.0F;
		//subtotal est juste une trasient creer dans CartItem pour calculer le prix de l'article et de la quantité
		for(CartItem item : cartItems) {
			estimatedTotal += item.getSubTotal();
		}
		
		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		boolean usePrimaryAddressAsDefault = false;
		
		if(defaultAddress != null) {
			shippingRate = rateService.getShippingRateForAddress(defaultAddress);
		}else {
			usePrimaryAddressAsDefault = true;
			shippingRate = rateService.getShippingRateForCustomer(customer);
		}
		
		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
		model.addAttribute("shippingSupported", shippingRate != null);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("estimatedTotal", estimatedTotal);
		
		return "cart/shopping_cart";
	}
	
	//on a plus besoi de verifier si le customer exuiste vraiment puisqu'avant de cliquer ici qu'il est deja logué
	

}
