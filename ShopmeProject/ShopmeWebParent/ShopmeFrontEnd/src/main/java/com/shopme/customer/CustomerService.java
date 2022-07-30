package com.shopme.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.CountryRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<Country> findAll(){
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	public boolean checkEmailUnique(String email) {
		Customer customer = customerRepository.findByEmail(email);
		
		return customer == null;
	}
	
	
	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(AuthenticationType.DATABASE);
		
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		
		customerRepository.save(customer);
	}

	private void encodePassword(Customer customer) {
		String password = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(password);
	}
	
	public Customer getCustomerByEmail(String email) {
		return customerRepository.findByEmail(email);
	}
	
	public boolean verify(String verificationCode) {
		Customer customer = customerRepository.findByVerificationCode(verificationCode);
		if(customer == null || customer.isEnabled()) {
			return false;
		}else {
			customerRepository.enable(customer.getId());
			return true;
		}
	}
	
	//si son type d'authentification est differente de ce qu'on envoit actuellement là on met a jour
	public void updateAuthentificationType(Customer customer, AuthenticationType type) {
		if(!customer.getAuthenticationType().equals(type)) {
			customerRepository.updateAuthenticationType(customer.getId(), type);
		}
	}
	

	public void addNewCustomerUponOauth2Login(String name, String email, String countryCode, AuthenticationType authenticationType) {

		Customer customer =new Customer();
		customer.setEmail(email);
		
		setName(name, customer);
		
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(authenticationType);
		customer.setPassword("");
		customer.setAddressLine1("");
		customer.setAddressLine2("");
		customer.setCity("");
		customer.setPostalCode("");
		customer.setState("");
		customer.setPhoneNumber("");
		customer.setCountry(countryRepository.findByCode(countryCode));
		
		customerRepository.save(customer);
	}
	
	public void setName(String name, Customer customer) {
		String[] nameArray = name.split(" ");
		if(nameArray.length < 2) {
			customer.setFirstName(name);
			customer.setLastName("");
		}else {
			String firstName = nameArray[0];
			customer.setFirstName(firstName);
			
			String lastName = name.replaceFirst(firstName + " ", "");
			customer.setLastName(lastName);
		}
	}
	
	
	public void updateCustomer(Customer customerInForm) {
		Customer customerInDb = customerRepository.findById(customerInForm.getId()).get();
		if(customerInDb.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
			
		
		if (!customerInForm.getPassword().isEmpty()) {
			String passwordNew = passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(passwordNew);
		}else {
			customerInForm.setPassword(customerInDb.getPassword());
		}
	   }else {
		   customerInForm.setPassword(customerInDb.getPassword());
	   }
		
		customerInForm.setCreatedTime(customerInDb.getCreatedTime());
		customerInForm.setVerificationCode(customerInDb.getVerificationCode());
		customerInForm.setEnabled(customerInDb.isEnabled());
		customerInForm.setAuthenticationType(customerInDb.getAuthenticationType());
		customerInForm.setResetPasswordTokebn(customerInDb.getResetPasswordTokebn());
		
	   customerRepository.save(customerInForm);
	}

	public String  updateResetPasswordToken(String email) throws CustomerNotFoundException {
         Customer customer = customerRepository.findByEmail(email);
         if(customer != null) {
        	 String token = RandomString.make(30);
        	 customer.setResetPasswordTokebn(token);
        	 customerRepository.save(customer);
        	 
        	 return token;
         }else {
        	 throw new CustomerNotFoundException("no customer is found with the given email "+ email);
         }
		
	}
	
	public Customer getBYresetPasswordToken(String token) {
		return customerRepository.findByResetPasswordTokebn(token);
	}
	
	public void updatePassword(String token, String newPassword) throws CustomerNotFoundException {
	
		Customer customer = customerRepository.findByResetPasswordTokebn(token);
		if(customer == null) {
			throw new CustomerNotFoundException("customer not found with the given token");
		}
		
		customer.setPassword(newPassword);
		encodePassword(customer);
		customer.setResetPasswordTokebn(null);
		customerRepository.save(customer);
		
	}
	
	
	
}
