package com.shopme.admin.address;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace =  Replace.NONE)
@Rollback(false)
public class AddressRepositoryTest {
	
	//@Autowired private AddressRepository addressRepository;
	@Autowired private TestEntityManager entityManager;
	
	@Test
	public void testCreateAddress() {
		Customer customer = entityManager.find(Customer.class, 2);
		Country countryId = entityManager.find(Country.class, 1);
		
		Address a = new Address();
		a.setFirstName("jean 2");
		a.setLastName("luc 2");
		a.setAddressLine1("6788865454");
		a.setAddressLine2("908525656");
		a.setPhoneNumber("696676655");
		a.setCity("DOUALA");
		a.setState("Edea");
		a.setPostalCode("avenue 14");
		a.setDefaultForShipping(true);
		a.setCountry(countryId);
		a.setCustomer(customer);
		
		//Address save = addressRepository.save(a);
		
		//assertThat(save.getId()).isGreaterThan(0);
	}

}
