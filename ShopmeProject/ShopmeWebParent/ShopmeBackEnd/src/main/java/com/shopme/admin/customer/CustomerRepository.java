package com.shopme.admin.customer;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Customer;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {
	
	public  Long countByid(Integer id);
	
	
	@Query("UPDATE Customer c set c.enabled = ?2 where c.id = ?1")
	@Modifying
	public void updateEnableStatus(Integer id, boolean enabled);
	
	@Query("SELECT c FROM Customer c where c.email = ?1")
	public Customer findByEmail(String email);
	
	@Query("SELECT c FROM Customer c WHERE CONCAT (c.email, ' ', c.firstName, ' ',c.lastName, ' ', c.addressLine1,' ',"
			+ "c.addressLine2, ' ',c.city, ' ', c.state, ' ', c.postalCode, ' ',c.country.name) LIKE %?1% ")
    public Page<Customer> findAll(String keyword, Pageable pageable);

}
