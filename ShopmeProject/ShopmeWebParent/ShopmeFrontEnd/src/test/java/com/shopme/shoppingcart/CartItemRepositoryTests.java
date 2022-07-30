package com.shopme.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CartItemRepositoryTests {
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testSaveItems() {
		
		Integer customerId = 1;
		Integer productId = 1;
		
		Product product = entityManager.find(Product.class, productId);
		Customer customer = entityManager.find(Customer.class, customerId);
		
		CartItem cartItem = cartItemRepository.save(new CartItem(customer, product, 5));
		
		assertThat(cartItem.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testSaveItems2() {
		
		Integer customerId = 10;
		Integer productId = 10;
		
		Product product = entityManager.find(Product.class, productId);
		Customer customer = entityManager.find(Customer.class, customerId);
		
		CartItem cartItem = new CartItem();
		cartItem.setCustomer(customer);
		cartItem.setProduct(product);
		cartItem.setQuantity(2);
		
		CartItem cartItem2 = new CartItem();
		cartItem2.setCustomer(customer);
		cartItem2.setProduct(new Product(8));
		cartItem2.setQuantity(3);
		
		Iterable<CartItem> saveAll = cartItemRepository.saveAll(List.of(cartItem, cartItem2));
		
		assertThat(saveAll).size().isGreaterThan(0);
		
	}
	
	@Test
	public void testFindByCustomer() {
		Customer c = entityManager.find(Customer.class, 10);
		List<CartItem> cartItem = cartItemRepository.findByCustomer(c);
		cartItem.forEach(System.out :: println);
	}
	
	@Test
	public void testGetSingleCartItem() {
		Integer customerId = 10;
		Integer productId = 10;
		
		CartItem cartItem = cartItemRepository.findByCustomerAndProduct(new Customer(customerId),new Product(productId));
		
		System.out.println("cart itme : "+ cartItem );
		
		assertThat(cartItem).isNotNull();
		
	}
	
	
	@Test
	public void testUpdateQuantity() {
		Integer customerId = 10;
		Integer productId = 10;
		Integer quantity = 20;
		Integer id = 2;
		
		cartItemRepository.updateQuantity(quantity, customerId, productId);
		
		CartItem c= cartItemRepository.findById(id).get();
		
		assertThat(c.getQuantity()).isEqualTo(20);
	}
	
	@Test
	public void testdeleteByCustomerAndProduct() {
		Integer customerId = 1;
		Integer  productId = 1;
		Integer id = 1;
		
		cartItemRepository.deleteByCustomerAndProduct(customerId, productId);
		
		Optional<CartItem> cartItem = cartItemRepository.findById(id);
		
		assertThat(cartItem).isEmpty();
	}
	
	


}
