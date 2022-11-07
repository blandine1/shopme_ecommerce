package com.shopme.admin.shippingrate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.admin.product.ProductRepository;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class ShippingRateServiceTest {

	@MockBean private ShippingRateRepository rateRepository;
	@MockBean private ProductRepository productRepository;
	
	@InjectMocks
	private ShippingRateSrvice rateSrvice;
	
	@Test
	public void testCalculateShippingCost_NoRayeFound() {
		Integer productId = 1;
		Integer countryId = 234;
		String state = "ABCDE";
		
		Mockito.when(rateRepository.findByCountryAndState(countryId, state)).thenReturn(null);
		
		assertThrows(ShippingRateNotFoundException.class, new Executable() {
			
			@Override
			public void execute() throws Throwable {
				rateSrvice.calculateShippingCoast(productId, countryId, state);
			}
		});
	}
	
	@Test
	public void testCalculateShippingCost_RateFound() throws ShippingRateNotFoundException {
      Integer productId = 1;
      Integer countryId = 48;
      String state = "DOUALA";
      
      ShippingRate shippingRate = new ShippingRate();
      shippingRate.setRate(10);
      
      Mockito.when(rateRepository.findByCountryAndState(countryId, state)).thenReturn(shippingRate);
      
      Product product = new Product();
      product.setWeigth(5);
      product.setWidth(4);
      product.setHeigth(3);
      product.setLength(8);
      
      Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
      
      float shippingCost = rateSrvice.calculateShippingCoast(productId, countryId, state);
      
      assertEquals(50, shippingCost);
      
	} 
	
	
	
	
	
}
