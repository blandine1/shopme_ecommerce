package com.shopme.shipping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;

@Service
public class ShippingRateService {
	
	@Autowired private ShippingRateRepository rateRepository;
	
	//shipping rate refert au taux de livraison
	//cette fonction premet de recuperer le taux de livraison appatenant a ce client
	public ShippingRate getShippingRateForCustomer(Customer customer ) {
		String state = customer.getState();
		if(state ==null || state.isEmpty()) {
			state =customer.getCity();
		}
		
		return rateRepository.findByCountryAndState(customer.getCountry(), state);
	}
	
	//cette fonction premet de recuperer le taux de livraison appatenant a un certain point de livraison
	public ShippingRate getShippingRateForAddress(Address address ) {
		String state = address.getState();
		if(state ==null || state.isEmpty()) {
			state =address.getCity();
		}
		
		return rateRepository.findByCountryAndState(address.getCountry(), state);
	}

}
