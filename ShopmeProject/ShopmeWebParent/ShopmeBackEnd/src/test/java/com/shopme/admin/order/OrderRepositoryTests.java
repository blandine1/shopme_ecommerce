package com.shopme.admin.order;

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

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderRepositoryTests {
	
	@Autowired private OrderRepository orderRepository;
	@Autowired private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewOrderwithSingleProduct() {
		
		Customer customer = entityManager.find(Customer.class, 1);
		Product product = entityManager.find(Product.class, 1);
		Order mainOrder = new Order();
		
		mainOrder.setCustomer(customer);
		mainOrder.setOrderTime(new Date());
		mainOrder.copyAddressFromCustomer();
	    
	    mainOrder.setShippingCost(10);
	    mainOrder.setProductCost(product.getCost());
	    mainOrder.setTax(0);
	    mainOrder.setSubtotal(product.getPrice());
	    mainOrder.setTotal(product.getPrice() + 10);
	    
	    mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
	    mainOrder.setStatus(OrderStatus.NEW);
	    mainOrder.setDeliverDate(new Date());
	    mainOrder.setDeliverDays(4);
	    
	    OrderDetail orderDetail= new OrderDetail();
	    orderDetail.setProduct(product);
	    orderDetail.setOrder(mainOrder);
	    orderDetail.setProductCost(product.getCost());
	    orderDetail.setQuantity(1);
	    orderDetail.setShippingCost(10);
	    orderDetail.setSubtotal(product.getPrice());
	    orderDetail.setUnitPrice(product.getPrice());
	    
	    mainOrder.getOrderDetails().add(orderDetail);
	    
	    Order savedOrder = orderRepository.save(mainOrder);
	    
	    assertThat(savedOrder.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateNewOrderwithMultipleProduct() {
		Customer customer = entityManager.find(Customer.class, 39);
		Product product1 = entityManager.find(Product.class, 28);
		Product product2 = entityManager.find(Product.class, 42);
		
		Order mainOrder = new Order();
		
		mainOrder.setCustomer(customer);
		mainOrder.setOrderTime(new Date());
		mainOrder.copyAddressFromCustomer();
		
		 OrderDetail orderDetail1= new OrderDetail();
		 orderDetail1.setProduct(product1);
		 orderDetail1.setOrder(mainOrder);
		 orderDetail1.setProductCost(product1.getCost());
		 orderDetail1.setQuantity(1);
		 orderDetail1.setShippingCost(10);
		 orderDetail1.setSubtotal(product1.getPrice());
		 orderDetail1.setUnitPrice(product1.getPrice());
		 
		 OrderDetail orderDetail2= new OrderDetail();
		 orderDetail2.setProduct(product2);
		 orderDetail2.setOrder(mainOrder);
		 orderDetail2.setProductCost(product2.getCost());
		 orderDetail2.setQuantity(2);
		 orderDetail2.setShippingCost(20);
		 orderDetail2.setSubtotal(product2.getPrice() * 2);
		 orderDetail2.setUnitPrice(product2.getPrice());
		 
		 mainOrder.getOrderDetails().add(orderDetail1);
		 mainOrder.getOrderDetails().add(orderDetail2);
		 
		  mainOrder.setShippingCost(10);
		  mainOrder.setProductCost(product1.getCost() + product2.getCost());
		  mainOrder.setTax(0);
		  float subtotal = product1.getPrice() + product2.getPrice() * 2;
	      mainOrder.setSubtotal(subtotal);
		  mainOrder.setTotal(subtotal + 30);
		  
		  mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		  mainOrder.setStatus(OrderStatus.DELIVERED);
		  mainOrder.setDeliverDate(new Date());
		  mainOrder.setDeliverDays(1);
		  
		  
		  
		  Order savedOrder = orderRepository.save(mainOrder);
		  
		  assertThat(savedOrder.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListOrders() {
		Iterable<Order> orders = orderRepository.findAll();
		
		assertThat(orders).size().isGreaterThan(0);
		
		orders.forEach(System.out :: println);
	}
	
	@Test
	public void testUpdateOrder() {
		Integer id = 2;
		Order order = orderRepository.findById(id).get();
		
		order.setPaymentMethod(PaymentMethod.COD);
		order.setDeliverDays(3);
		order.setDeliverDate(new Date());
		order.setStatus(OrderStatus.SHIPPING);
		order.setOrderTime(new Date());
		
		Order save = orderRepository.save(order);
		
		assertThat(save.getStatus()).isEqualTo(OrderStatus.SHIPPING);
	}
	
	@Test
	public void getSingleOrder() {
		Integer id = 2;
		Order order = orderRepository.findById(id).get();
		
		System.out.println(order);
		
		assertThat(order).isNotNull();
	}
	
	@Test
	public void delete() {
		Integer id = 3;
		orderRepository.deleteById(id);
		
		Optional<Order> findById = orderRepository.findById(id);
		
		assertThat(findById).isNull();
	}

}
