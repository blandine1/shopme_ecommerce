package com.shopme.customer;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;

@Controller
public class ForgotPasswordController {

	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private SettingService settingService;
	
	@GetMapping("/forgot_password")
	public String showRequestForm() {
		
		return "customer/forgot_password_form";
	}
	
	@PostMapping("/forgot_password")
	public String processRequestForm(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		try {
			String token = customerService.updateResetPasswordToken(email);
			String link = Utility.getSiteUrl(request)+ "/reset_password?token="+ token;
			
			sendMail(link, email);
			
			model.addAttribute("message", "we have send message to your email please check!!");
		} catch (CustomerNotFoundException e) {
			model.addAttribute("error", e.getMessage());
		} catch (UnsupportedEncodingException  | MessagingException e) {
			model.addAttribute("message", "Could not send email message");
		}
		
		return "customer/forgot_password_form";
	}
	
	private void sendMail(String link, String email) throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAddress = email;
		String subject = "here is the link to reset your password";
		
		String content = "<p>Hello,</p>"
				+ "<p>You have requested to reset your password.</p>"
				+ "<p>click the link bellow to change your password</p>"
				+ "<p><a href=\""+link+"\">change my password</a></p>"
				+ "<p>Ignore this if you haven't request to change your password or you remember your password</p>";
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		helper.setText(content, true);
		
		mailSender.send(message);
	}
	
	@GetMapping("/reset_password")
	public String showResetForm(@Param("token") String token, Model model) {
		
		Customer customer = customerService.getBYresetPasswordToken(token);
	
		if(customer != null) {
			model.addAttribute("token", token);
		}else {
			model.addAttribute("pageTitle", "Invalid Token");
			return "message";
		}
		
		System.out.println("email " + customer.getEmail());
		return "customer/reset_password_form";
	}
	
	@PostMapping("/reset_password")
	public String processResetForm(HttpServletRequest request, Model model) {
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		
		try {
			customerService.updatePassword(token, password);
			model.addAttribute("pageTitle", "Reset your password");
			model.addAttribute("title", "Reset password");
			model.addAttribute("message", "You have changed your password successfully");
			
			return "message";
		} catch (CustomerNotFoundException e) {
			model.addAttribute("pageTitle", "invalid token");
			model.addAttribute("message", e.getMessage());
			return "message";
		}
	}
	
	
	
}
