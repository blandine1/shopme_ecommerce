package com.shopme.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.security.CustomerUserDetails;
import com.shopme.security.oauth.CustomerOauth2User;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;

@Controller
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private SettingService settingService;
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		
		List<Country> listCountries = customerService.findAll();
		
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Customer registration");
		model.addAttribute("customer", new Customer());
		
		return "register/register_form";
	}
	
	@PostMapping("/create_customer")
	public String registerCustomer(Customer customer,Model model, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		customerService.registerCustomer(customer);
		sendVerificationEmail(request,customer);
		
		model.addAttribute("pageTitle", "Registreation succeed");
		return "register/register_success";
	}
 
	private void sendVerificationEmail(HttpServletRequest request,Customer customer) throws UnsupportedEncodingException, MessagingException {

		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAdress = customer.getEmail();
		String subjct = emailSettings.getCustomerVerifySubject();
		String content = emailSettings.getCustomerVerifyContent();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAdress);
		helper.setSubject(subjct);
		
		content = content.replace("[[name]]", customer.getFullName());
		
		String verifyUrl = Utility.getSiteUrl(request) + "/verify?code=" + customer.getVerificationCode();
		content= content.replace("[[URL]]", verifyUrl);
		helper.setText(content, true);
		mailSender.send(message);
		
		System.out.println("to adress "+toAdress);
		System.out.println("verify URL : "+verifyUrl);
		
	}
	
	@GetMapping("/verify")
	public String verifyAccount(@Param("code")String code, Model model) {
		boolean verified = customerService.verify(code);
		
		return "register/"+ (verified ? "verify_success" : "verify_fail");
	}
	
	@GetMapping("/account_details")
	public String viewAccountDetails(Model model, HttpServletRequest request) {
		
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		 Customer customer = customerService.getCustomerByEmail(email);
		 List<Country> listCountries = customerService.findAll();
		 model.addAttribute("customer", customer);
		 model.addAttribute("listCountries", listCountries);
		 
		return "customer/account_form";
	}
	

	@PostMapping("/update_account_details")
	public String updateAccountDetails(Model model, Customer customer, RedirectAttributes attributes, HttpServletRequest request) {
		
		customerService.updateCustomer(customer);
		attributes.addFlashAttribute("message", "mise a jour effectuée avec succes");
		
		updateNameForAuthenticatedCustomer(customer, request);
		
		String redirectOption = request.getParameter("redirect");
		String redirectUrl = "redirect:/account_details";
		
		if("address_book".equals(redirectOption)) {
			redirectUrl = "redirect:/address_book";
		}else if("cart".equals(redirectOption)) {
			redirectUrl = "redirect:/cart";
		}
		
		return redirectUrl;
	}

	private void updateNameForAuthenticatedCustomer(Customer customer,HttpServletRequest request) {
		 Object principal = request.getUserPrincipal();
		 String fullName = customer.getFirstName() +" " + customer.getLastName();
		 //a ce niveau si le user se connecte avec son userName et  le rememberMe option
		 if(principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken ) {
          
		  CustomerUserDetails userDetails =getCustomerUserDetailObjet(principal);
          Customer authenticatedCustomer = userDetails.getCustomer();
          authenticatedCustomer.setFirstName(customer.getFirstName());
          authenticatedCustomer.setLastName(customer.getLastName());
			 
		 }else if(principal instanceof OAuth2AuthenticationToken){
			 OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) principal;
			 CustomerOauth2User oauth2User = (CustomerOauth2User) oAuth2Token.getPrincipal();
			 oauth2User.setFullName(fullName);		 
		}
	}
	
	private CustomerUserDetails getCustomerUserDetailObjet(Object principal) {
		CustomerUserDetails userdetails = null;
		if(principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
			userdetails = (CustomerUserDetails) token.getPrincipal();
		}else if(principal instanceof RememberMeAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
			userdetails = (CustomerUserDetails) token.getPrincipal();
		}
		
		return userdetails;
	}
	
}
