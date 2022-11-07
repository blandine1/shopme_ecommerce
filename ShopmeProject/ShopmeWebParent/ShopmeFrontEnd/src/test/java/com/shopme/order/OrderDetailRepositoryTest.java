package com.shopme.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.common.entity.order.OrderStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class OrderDetailRepositoryTest {
	
	@Autowired private OrderDetailRepository orderDetailRepository;
	
	@Test
	public void testcountByProductAndCustomerAndOrderStatus() {
		Integer productId = 99;
		Integer customerId = 1;
		
		Long count = orderDetailRepository.countByProductAndCustomerAndOrderStatus(productId, customerId, OrderStatus.DELIVERED);
		System.out.println("cont : "+ count);
		assertThat(count).isGreaterThan(0);
		
	}

}
