package com.shopme.common.entity.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.shopme.common.Constants;
import com.shopme.common.entity.IdBaseEntity;

@Entity
@Table(name = "product_images")
public class ProductImage extends IdBaseEntity{

	
	@Column(nullable = false)
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	//constructors
	
	public ProductImage() {
	}
	
	public ProductImage(String name, Product product) {
		this.name = name;
		this.product = product;
	}
	
	public ProductImage(int id, String name, Product product) {
		this.id = id;
		this.name = name;
		this.product = product;
	}

	//getter and setter

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	@Transient
	public String getImagePath() {
		
		return  Constants.S3_BASE_URI + "/product-images/"+ product.getId() + "/extras/" + this.name;
	}
	
	
}
