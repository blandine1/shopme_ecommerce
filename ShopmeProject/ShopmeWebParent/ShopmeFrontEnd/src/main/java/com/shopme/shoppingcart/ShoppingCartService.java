package com.shopme.shoppingcart;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;
import com.shopme.product.ProductRepository;

@Service
@Transactional
public class ShoppingCartService {
	
	@Autowired
	private CartItemRepository cartItemRepository;
	 @Autowired private ProductRepository productRepository;
	 
	
 	public Integer addProduct(Integer productId, Integer quantity, Customer customer ) throws ShoppingCartException {
		Integer updataedQuantity = quantity;
		
		Product product =new Product(productId);
		
		CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
		if(cartItem != null) {
			updataedQuantity = cartItem.getQuantity() + quantity;
			
			if(updataedQuantity > 5) {
				throw new ShoppingCartException("could not add more "+ quantity + " item(s) because there's already "+cartItem.getQuantity()
				+" item(s) in your shopping cart. Maximum allow is 5");
			}
		}else {
			cartItem = new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);
		}
		
		cartItem.setQuantity(updataedQuantity);
		
		cartItemRepository.save(cartItem);
		
		
		return updataedQuantity;
	}
	
	public List<CartItem> listCartItem(Customer customer){
		return cartItemRepository.findByCustomer(customer);
	}
	
	public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
		cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
		Product product = productRepository.findById(productId).get();
		float discountPrice = product.getDiscountPrice() * quantity;
		
		return discountPrice;
	}
	
	public void removeProduct(Integer productId, Customer customer) {
		cartItemRepository.deleteByCustomerAndProduct(customer.getId(), productId);
	}
	
	public void deleteByCustomer(Customer customer) {
		cartItemRepository.deleteByCustomer(customer.getId());
	}
	
	

}
