package com.shopme.checkout;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;

@Service
public class CheckOutService {
	
	private static final int DIM_DIVISOR = 139; 
	
	public CheckOutInfo prepareCheckOut(List<CartItem>cartItems, ShippingRate shippingRate) {
		CheckOutInfo checkOutInfo = new CheckOutInfo();
		
		float productCost = calculateProductCost(cartItems);
		float productTotal = calculateProductTotal(cartItems);
		float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
		float paymentTotal = productTotal + shippingCostTotal;
		
		checkOutInfo.setProductCost(productCost);
		checkOutInfo.setProductTotal(productTotal);
		checkOutInfo.setDeliverDays(shippingRate.getDays());
		checkOutInfo.setCodSupported(shippingRate.isCodeSupported());
		checkOutInfo.setShippingCostTotal(shippingCostTotal);
		checkOutInfo.setPaymentTotal(paymentTotal);
		
		return checkOutInfo;
		
	}

	private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
		float shippingCostTotal = 0.0f;
		
		for(CartItem item : cartItems) {
			Product product = item.getProduct();
			
			float dimweight = (product.getLength() * product.getWeigth() * product.getHeigth()) / DIM_DIVISOR;
			float finalweight = product.getWeigth() > dimweight ? product.getWeigth() : dimweight;
		    float shippingCost = finalweight * item.getQuantity() * shippingRate.getRate();
		    
		    item.setShippingCost(shippingCost);
			
			shippingCostTotal += shippingCost;
		}
		
		return shippingCostTotal;
	}

	private float calculateProductTotal(List<CartItem> cartItems) {
		
        float total = 0.0f;
		
		for(CartItem item: cartItems) {
			total += item.getSubTotal();
		}
		return total;
	}

	private float calculateProductCost(List<CartItem> cartItems) {
		
		float cost = 0.0f;
		
		for(CartItem item: cartItems) {
			cost += item.getQuantity() * item.getProduct().getCost();
		}
		return cost;
	}

}
