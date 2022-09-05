package com.shopme;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.shopme.security.oauth.CustomerOauth2User;
import com.shopme.setting.CurrencySettingBag;
import com.shopme.setting.EmailSettingBag;

public class Utility {
	public static String getSiteUrl(HttpServletRequest request) {
		String siteUrl = request.getRequestURL().toString(); 
		
		return siteUrl.replace(request.getServletPath(), "");
	}
	
	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settings) {
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(settings.getHost());
		mailSender.setPort(settings.getPort());
		mailSender.setUsername(settings.getUsername());
		mailSender.setPassword(settings.getPassword());
		
		Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.smtp.auth", settings.getSmtpAuth());
		mailProperties.setProperty("mail.smtp.starttls.enable", settings.getSmtpSecured());
		
		mailSender.setJavaMailProperties(mailProperties);
		
		return mailSender;
	}
	
	public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
		 Object principal = request.getUserPrincipal();
		 if(principal == null) return null;
		 String customerEmail = null;
		 //a ce niveau si le user se connecte avec son userName et  le rememberMe option
		 if(principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken ) {
			 customerEmail = request.getUserPrincipal().getName();
		 }else if(principal instanceof OAuth2AuthenticationToken){
			 OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) principal;
			 CustomerOauth2User oauth2User = (CustomerOauth2User) oAuth2Token.getPrincipal();
			 customerEmail = oauth2User.getEmail();
		 }
		 
		 return customerEmail;
	}
	
	public static String formatCurrency(float amount, CurrencySettingBag settingBag) {
		String symbol = settingBag.getSymbol();
		String symbolPosition = settingBag.getSymbolPosition();
		String decimalPointType = settingBag.getDecimalPointType();
		String thousandPointType = settingBag.getThousandPointType();
		int decimalDigits = settingBag.getDecimalDigits();
		
		String pattern = symbolPosition.equals("Before Price") ? symbol : "";
		pattern += "###,###";
		if(decimalDigits > 0) {
			pattern += ".";
			for(int count = 1; count <= decimalDigits; count++) pattern += "#";
		}
		
		pattern += symbolPosition.equals("After Price") ? symbol : "";
		
		char thousandsSeparator = thousandPointType.equals("POINT") ? '.' : ',';
		char decimalSeoarator = decimalPointType.equals("POINT") ? '.' : ',';
		
		DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
		decimalFormatSymbols.setDecimalSeparator(decimalSeoarator);
		decimalFormatSymbols.setGroupingSeparator(thousandsSeparator);
		
		DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);
		
		return formatter.format(amount);
	}
	
}


