package com.shopme.admin.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.order.OrderDetail;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderDetailRepositoryTest {

	
	@Autowired
	private OrderDetailRepository detailRepository;
	
	@Test
	public void testfindWithCategoryAndTimeBetween() throws ParseException {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = dateFormatter.parse("2021-08-01");
		Date endDate = dateFormatter.parse("2021-08-31");
		
		List<OrderDetail> listOrderDetails = detailRepository.findWithCategoryAndTimeBetween(startDate, endDate);
		
		assertThat(listOrderDetails.size()).isGreaterThan(0);
		
		for(OrderDetail detail: listOrderDetails) {
			System.out.printf("%-30s | %d | %10.2f | %10.2f | %10.2f \n",
					detail.getProduct().getCategory().getName(), detail.getQuantity(), detail.getProductCost(), detail.getShippingCost(), detail.getSubtotal());
		}
	}
	
	@Test
	public void testfindWithProductAndTimeBetween() throws ParseException {
		DateFormat dateFormatter = new SimpleDateFormat("yyy-MM-dd");
		Date startDate = dateFormatter.parse("2021-10-28");
		Date endDate = dateFormatter.parse("2021-10-30");
		
		List<OrderDetail> listOrderDetails = detailRepository.findWithProductAndTimeBetween(startDate, endDate);
		
		assertThat(listOrderDetails.size()).isGreaterThan(0);
		
		for(OrderDetail detail: listOrderDetails) {
			System.out.printf("%-30d | %s | %10.2f | %10.2f | %10.2f \n",
					detail.getQuantity(),detail.getProduct().getShortName(), detail.getProductCost(), detail.getShippingCost(), detail.getSubtotal());
		}
	}
}
