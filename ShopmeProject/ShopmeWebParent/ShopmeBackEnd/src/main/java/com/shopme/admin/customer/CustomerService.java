package com.shopme.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@Service
@Transactional
public class CustomerService {
	public final static int CUSTOMER_PER_PAGE = 10;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public Page<Customer> listByPage(int pageNum,String keyword, String sortField, String sortDir){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, CUSTOMER_PER_PAGE, sort);
		
		if(keyword != null) {
			return customerRepository.findAll(keyword, pageable);
		}
		
		return customerRepository.findAll(pageable);
	}
	
	public void UpdateCustomerEnabledStatus(Integer id,boolean status) {
		 customerRepository.updateEnableStatus(id, status);
	}
	
	public Customer getSingle(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CustomerNotFoundException("no customer is found with the given Id "+ id);
		}
	}
	
	//la luste des pays
	public List<Country> listAllCountries(){
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		Customer findByEmail = customerRepository.findByEmail(email);
		
		if (findByEmail != null && findByEmail.getId() != id) {
			//a ce niveau il est deja en bd
			return false;
		}
		return true;
	}
	
	public Customer saveCustomer(Customer customerInForm) {
		Customer customerInDb = customerRepository.findById(customerInForm.getId()).get();
		if (!customerInForm.getPassword().isEmpty()) {
			String passwordNew = passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(passwordNew);
		}else {
			customerInForm.setPassword(customerInDb.getPassword());
		}
		
		customerInForm.setCreatedTime(customerInDb.getCreatedTime());
		customerInForm.setVerificationCode(customerInDb.getVerificationCode());
		customerInForm.setEnabled(customerInDb.isEnabled());
		customerInForm.setAuthenticationType(customerInDb.getAuthenticationType());
		customerInForm.setResetPasswordTokebn(customerInDb.getResetPasswordTokebn());
		
		return customerRepository.save(customerInForm);
	}
	
	
	public void deleteCustomer(Integer id) throws CustomerNotFoundException {
		Long countByid = customerRepository.countByid(id);
		if(countByid == null || countByid == 0) {
			throw new CustomerNotFoundException("no customer is found with your given id "+ id);
		}
		
		customerRepository.deleteById(id);
	}
}
