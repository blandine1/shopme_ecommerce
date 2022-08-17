package com.shopme.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ShippingRateRepositoryTest {
	
	@Autowired private ShippingRateRepository rateRepository;
	
	@Test
	public void testFindByCountryAndState() {
		Country usa = new Country(234);
		String state = "New York";
		
		ShippingRate rate = rateRepository.findByCountryAndState(usa, state);
		
		assertThat(rate).isNotNull();
		System.out.println(rate);
	}

}
