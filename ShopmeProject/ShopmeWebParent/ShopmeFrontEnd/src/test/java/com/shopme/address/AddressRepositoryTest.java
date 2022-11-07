package com.shopme.address;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Address;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTest {
	
	@Autowired private AddressRepository addressRepository;
	
	@Test
	public void testUpdateDefaultAddress() {
		Integer id =4;
		Integer customerId = 39;
		
		addressRepository.setDefaultaddress(id);
		addressRepository.setNonDefaultForShipping(id, customerId);
		
	}
	
	@Test
	public void testGetDefaultAddressForCustomer() {
		Integer customerId = 39;
		Address address = addressRepository.findDefaultForCustomer(customerId);
		System.out.println(address);
	}

}
