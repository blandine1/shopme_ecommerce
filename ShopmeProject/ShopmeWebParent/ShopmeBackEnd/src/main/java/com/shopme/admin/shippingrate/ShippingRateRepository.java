package com.shopme.admin.shippingrate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.ShippingRate;

public interface ShippingRateRepository extends SearchRepository<ShippingRate, Integer>{
       
	@Query("SELECT sr from ShippingRate sr where sr.country.id =?1 and sr.state = ?2")
	public ShippingRate findByCountryAndState(Integer countryId, String state);
	
	@Modifying
	@Query("UPDATE ShippingRate sr set sr.codeSupported = ?2 WHERE sr.id = ?1")
	public void updateCODSupport(Integer id, boolean enabled);
	
	@Query("SELECT sr FROM ShippingRate sr WHERE sr.country.name LIKE %?1%  OR sr.state like %?1%")
	public Page<ShippingRate> findAll(String keyword, Pageable pageable);
	
	public Long countById(Integer id);
}
