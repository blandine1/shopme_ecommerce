package com.shopme.customer;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

	@Query("SELECT c from Customer c where c.email = ?1")
	public Customer findByEmail(String email);
	
	@Query("SELECT c from Customer c WHERE c.verificationCode = ?1")
	public Customer findByVerificationCode(String code);
	
	@Query("UPDATE Customer c set c.enabled = true, c.verificationCode= null WHERE c.id =?1")
	@Modifying
	public void enable(Integer id); 
	
	@Query("UPDATE Customer c SET c.authenticationType = ?2 where c.id = ?1")
	@Modifying
	public void updateAuthenticationType(Integer customerId, AuthenticationType type);
	
	 public Customer findByResetPasswordTokebn(String resetPasswordTokebn);
}
