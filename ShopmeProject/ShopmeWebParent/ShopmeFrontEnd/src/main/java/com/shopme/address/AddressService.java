package com.shopme.address;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;

@Service
@Transactional
public class AddressService {
	
	@Autowired private AddressRepository addressRepository;
	@Autowired private CustomerService customerService;
	
	public List<Address> listAddressBOok(Customer customer){
		return addressRepository.findByCustomer(customer);
	}
	
	public void save(Address address) {
		 addressRepository.save(address);
	}
	
	public Address get(Integer addressId, Integer customerId) {
		return addressRepository.findByIdAndCustomer(addressId, customerId);
	}
	
	public void deletAdress(Integer addressId, Integer customerId) {
		addressRepository.deleteByIdAndCustomer(addressId, customerId);
	}
	
	public void setDefaultAddress(Integer dfaultAddressId, Integer CustomerId) {
		if(dfaultAddressId > 0) {
			addressRepository.setDefaultaddress(dfaultAddressId);
		}
		
		addressRepository.setNonDefaultForShipping(dfaultAddressId, CustomerId);
	}
	
	public Address getDefaultAddress(Customer customer) {
		return addressRepository.findDefaultForCustomer(customer.getId());
	}
	

}
