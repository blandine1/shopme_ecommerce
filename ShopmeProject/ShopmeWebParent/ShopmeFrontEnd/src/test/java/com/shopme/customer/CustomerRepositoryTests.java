package com.shopme.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {

	@Autowired
	private CustomerRepository customerRepository;
	//cameroun id=48
	@Autowired
	private TestEntityManager  entityManager;
	
	@Test
	public void testCreateCustomer() {
		 Country country = entityManager.find(Country.class, 48);
		 
		 Customer customer = new Customer();
		 customer.setEmail("lucsonore@gmail.com");
		 customer.setPassword("lucsonore");
		 customer.setFirstName("luc");
		 customer.setLastName("sonore");
		 customer.setPhoneNumber("655792904");
		 customer.setAddressLine1("Rue des manguiers");
		 customer.setAddressLine2("addresse 2");
		 customer.setCity("NDOKOTTI");
		 customer.setState("DOUALA");
		 customer.setPostalCode("Post Code 123");
		 customer.setCreatedTime(new Date());
		 customer.setCountry(country);
		 
		 Customer savedCustomer = customerRepository.save(customer);
		 
		 assertThat(savedCustomer.getId()).isNotNull();
	}
	
	@Test
	public void testCreateCustomer2() {
		 Country country = entityManager.find(Country.class, 48);
		 
		 Customer customer = new Customer();
		 customer.setEmail("divine@gmail.com");
		 customer.setPassword("lucsonore");
		 customer.setFirstName("divine");
		 customer.setLastName("sonore");
		 customer.setPhoneNumber("655792904");
		 customer.setAddressLine1("Rue des manguiers france");
		 customer.setAddressLine2("addresse 2");
		 customer.setCity("NDOKOTTI");
		 customer.setState("DOUALA");
		 customer.setPostalCode("Post Code 1234");
		 customer.setVerificationCode("abc123");
		 customer.setCreatedTime(new Date());
		 customer.setCountry(country);
		 
		 Customer savedCustomer = customerRepository.save(customer);
		 
		 assertThat(savedCustomer.getId()).isNotNull();
	}
	
	@Test
	public void testCreateCustomer3() {
		 Country country = entityManager.find(Country.class, 48);
		 
		 Customer customer = new Customer();
		 customer.setEmail("test@gmail.com");
		 customer.setPassword("test");
		 customer.setFirstName("test");
		 customer.setLastName("sonore");
		 customer.setPhoneNumber("655792904");
		 customer.setAddressLine1("Rue des manguiers france");
		 customer.setAddressLine2("addresse 2");
		 customer.setCity("NDOKOTTI");
		 customer.setState("DOUALA");
		 customer.setPostalCode("Post Code 1234");
		 customer.setCreatedTime(new Date());
		 customer.setCountry(country);
		 
		 Customer savedCustomer = customerRepository.save(customer);
		 
		 assertThat(savedCustomer.getId()).isNotNull();
	}
	
	
	@Test
	public void testListCustomer() {
		Iterable<Customer> findAll = customerRepository.findAll();
		for(Customer c: findAll) {
			System.out.println(c);
		}
		
		assertThat(findAll).size().isGreaterThan(0);
	}
	
	@Test
	public void testUpdateCustomer() {
		Integer id = 2;
		Customer findById = customerRepository.findById(id).get();
		
		findById.setVerificationCode("abc123");
		
		Customer updateCustomer = customerRepository.save(findById);
		
		assertThat(updateCustomer.getFirstName()).isEqualTo("divine");
	}
	
	@Test
	public void testGetCustomer() {
		Integer id = 3;
		Optional<Customer> customer = customerRepository.findById(id);
		
		System.out.println(customer);
		
		assertThat(customer.isPresent());
	}
	
	@Test
	public void testDeleteCustomer() {
		Integer id = 3;
		
		customerRepository.deleteById(id);
		
		Optional<Customer> findById = customerRepository.findById(id);
		
		assertThat(findById.isEmpty());
	}
	
	@Test
	public void testFindByEmail() {
		String email = "divine@gmail.com";
		Customer customer = customerRepository.findByEmail(email);
		
		assertThat(customer.getEmail()).isEqualTo(email);
	}
	
	
	
	@Test
	public void testEnabledCustomer() {
		Integer id= 2;
		customerRepository.enaled(id);
		Customer customer = customerRepository.findById(id).get();
		assertThat(customer.isEnabled()).isTrue();
	}
	
	
	
	
	
	
	
	
	
}
