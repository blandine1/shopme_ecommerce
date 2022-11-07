package com.shopme.checkout;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.ControllerHelper;
import com.shopme.Utility;
import com.shopme.address.AddressService;
import com.shopme.checkout.paypal.PaypalApiException;
import com.shopme.checkout.paypal.PaypalService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderService;
import com.shopme.setting.CurrencySettingBag;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.PaymentSettingBag;
import com.shopme.setting.SettingService;
import com.shopme.shipping.ShippingRateService;
import com.shopme.shoppingcart.ShoppingCartService;

@Controller
public class CheckOutController {
	
	
	@Autowired private CheckOutService checkOutService;
	@Autowired private ControllerHelper controllerHelper;
	@Autowired private AddressService addressService;
	@Autowired private ShippingRateService shippingRateService;
	@Autowired private ShoppingCartService shoppingCartService;
	@Autowired private OrderService orderService;
	@Autowired private SettingService settingService;
    @Autowired private PaypalService paypalService;
	
	@GetMapping("/checkout")
	public String showCheckOutPage(Model model, HttpServletRequest request) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		
		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		
		if(defaultAddress != null) {
			model.addAttribute("shippingAddress", defaultAddress.toString());
			shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
		}else {
			model.addAttribute("shippingAddress", customer.toString());
			shippingRate = shippingRateService.getShippingRateForCustomer(customer);
		}
		
		if(shippingRate == null) {
			return "redirect:/cart";
		}
		
		List<CartItem> cartItems = shoppingCartService.listCartItem(customer);
		CheckOutInfo checkOutInfo = checkOutService.prepareCheckOut(cartItems, shippingRate);
		
		String currencyCode = settingService.getCurrencyCode();
		PaymentSettingBag paymentSettingBag = settingService.getPaymentSetting();
		String paypalClientId = paymentSettingBag.getClientId();
		
		model.addAttribute("paypalClientId", paypalClientId);
		model.addAttribute("customer", customer);
		model.addAttribute("currencyCode", currencyCode);
		model.addAttribute("checkOutInfo", checkOutInfo);
		model.addAttribute("cartItems", cartItems);
		
		return "checkout/checkout";
	}
	
	@PostMapping("/place_order")
	public String placeOrder(HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		 String paymentType = request.getParameter("paymentMethod");
		 PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);
		
			Customer customer = controllerHelper.getAuthenticatedCustomer(request);
			
			
			Address defaultAddress = addressService.getDefaultAddress(customer);
			ShippingRate shippingRate = null;
			
			if(defaultAddress != null) {
				shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
			}else {
				shippingRate = shippingRateService.getShippingRateForCustomer(customer);
			}
		
			
			List<CartItem> cartItems = shoppingCartService.listCartItem(customer);
			CheckOutInfo checkOutInfo = checkOutService.prepareCheckOut(cartItems, shippingRate);
			
			Order order = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkOutInfo);
			
			shoppingCartService.deleteByCustomer(customer);
			sendOrderConfirmationEmail(request, order);
		
		return "checkout/order_completed";
	}
	
	private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) throws UnsupportedEncodingException, MessagingException {
		
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		mailSender.setDefaultEncoding("utf-8");
		
		String toAddress = order.getCustomer().getEmail();
		String subject = emailSettings.getOrderConfirmationSubject();
		String content = emailSettings.getOrderConfirmationContent();
		
		subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));
		
		MimeMessage message = mailSender.createMimeMessage();
		
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
		String orderTime = dateFormat.format(order.getOrderTime());
		
		CurrencySettingBag currencySettings = settingService.getCurrencySettings();
		String totalAmount = Utility.formatCurrency(order.getTotal(), currencySettings);
		
		content = content.replace("[[name]]", order.getCustomer().getFullName());
		content = content.replace("[[orderId]]", String.valueOf(order.getId()));
		content = content.replace("[[orderTime]]", orderTime);
		content = content.replace("[[shippingAddress]]", order.getShippingAddress());
		content = content.replace("[[total]]", totalAmount);
		content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());
		
		helper.setText(content, true);
		mailSender.send(message);
	}

	
	
	@PostMapping("/process_paypal_order")
    public String processPayplaOrder(HttpServletRequest request, Model model) throws UnsupportedEncodingException, MessagingException {
		String orderId = request.getParameter("orderId");
		
		String pageTitle="checkout failure";
		String message = "";
		
			try {
				if(paypalService.validateOrder(orderId)) {
					return placeOrder(request);
				}else {
					pageTitle="checkout failure";
					message  ="Error: Transaction could not be completed because order information i invalid"; 
				}
				
			} catch (PaypalApiException e) {
				message = "Error: Transaction failed due to ";
			}
			
			model.addAttribute("pageTitle", pageTitle);
			model.addAttribute("message", message);
		
		return "message";
	}
	
	
    //on a plus besoi de verifier si le customer exuiste vraiment puisqu'avant de cliquer ici qu'il est deja logué


}
