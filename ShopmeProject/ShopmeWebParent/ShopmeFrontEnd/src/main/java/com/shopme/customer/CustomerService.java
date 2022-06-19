package com.shopme.customer;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.CountryRepository;

import net.bytebuddy.utility.RandomString;

@Service
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
		
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		
		System.out.println("generated string :"+randomCode);
	}

	private void encodePassword(Customer customer) {
		String password = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(password);
	}
	
	
	
	
	
	
	
	
	
	
	
}
