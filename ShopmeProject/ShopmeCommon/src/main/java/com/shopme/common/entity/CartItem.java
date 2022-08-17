package com.shopme.common.entity;

import java.beans.Transient;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.shopme.common.entity.product.Product;

@Entity
@Table(name = "cart_items")
public class CartItem extends IdBaseEntity{
	
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
	private int quantity;
	
	
	public CartItem() {
		
	}
	
	
	public CartItem(Integer id) {
		this.id = id;
	}



	public CartItem(Customer customer, Product product, int quantity) {
		this.customer = customer;
		this.product = product;
		this.quantity = quantity;
	}

	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "CartItem [id=" + id + ", customer=" + customer.getEmail() + ", product=" + product.getName() + ", quantity=" + quantity
				+ "]";
	}
	
    @Transient
	public float getSubTotal() {
    	return product.getDiscountPrice() * quantity;
    }
	
	
	

}
