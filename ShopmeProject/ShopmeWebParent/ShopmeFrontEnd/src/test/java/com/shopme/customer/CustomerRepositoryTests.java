package com.shopme.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Test
	public void testUpdateAuthenticationType() {
		int id = 1;
		Customer id2 = customerRepository.findById(id).get();
		
		id2.setAuthenticationType(AuthenticationType.FACEBOOK);
		
		Customer save = customerRepository.save(id2);
		
		assertThat(save.getAuthenticationType()).isEqualTo(AuthenticationType.FACEBOOK);
		
	}
}
