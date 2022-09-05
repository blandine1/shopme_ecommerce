package com.shopme.order;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.checkout.CheckOutInfo;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.common.entity.product.Product;

@Service
public class OrderService {
	
	@Autowired private OrderRepository orderRepository;
	
	public Order createOrder(Customer customer, Address address, List<CartItem> cartItems, 
			                      PaymentMethod paymentMethod, CheckOutInfo checkOutInfo) {
		Order newOrder =new Order();
		newOrder.setOrderTime(new Date());
		
		if(paymentMethod.equals(PaymentMethod.PAYPAL)) {
			newOrder.setStatus(OrderStatus.PAID);
		}else {
			newOrder.setStatus(OrderStatus.NEW);
		}
		
		
		newOrder.setCustomer(customer);
		newOrder.setProductCost(checkOutInfo.getProductCost());
		newOrder.setSubtotal(checkOutInfo.getProductTotal());
		newOrder.setShippingCost(checkOutInfo.getShippingCostTotal());
		newOrder.setTax(0.0f);
		newOrder.setTotal(checkOutInfo.getPaymentTotal());
		newOrder.setDeliverDate(checkOutInfo.getDeliverDate());
		newOrder.setDeliverDays(checkOutInfo.getDeliverDays());
		newOrder.setPaymentMethod(paymentMethod);
		
		 if(address == null) {
			 newOrder.copyAddressFromCustomer();
		 }else {
			 newOrder.copyShippingAddress(address);
		 }
		 
		 Set<OrderDetail> orderDetails = newOrder.getOrderDetails();
		 
		 for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();
			
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrder(newOrder);
			orderDetail.setProduct(product);
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setUnitPrice(product.getDiscountPrice());
			orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
			orderDetail.setSubtotal(cartItem.getSubTotal());
			orderDetail.setShippingCost(cartItem.getShippingCost());
			
			orderDetails.add(orderDetail);
		}
		
		return orderRepository.save(newOrder);
		
	}

}
