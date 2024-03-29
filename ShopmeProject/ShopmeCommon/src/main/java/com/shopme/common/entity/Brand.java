package com.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.shopme.common.Constants;

@Entity
@Table(name = "brands")
public class Brand extends IdBaseEntity{


	
	@Column(length = 45, unique = true, nullable = false)
	private String name;
	
	@Column(length = 128, nullable = false)
	private String logo;
	
	@ManyToMany
	@JoinTable(
			name = "brands_categories",
			joinColumns = @JoinColumn(name = "brand_id"),
		    inverseJoinColumns = @JoinColumn(name="category_id")
			)
	private Set<Category> categories =  new HashSet<>();
	
	//different constructor
	public Brand() {
	}
	
	
	public Brand(String name) {
		this.name = name;
	}
	
	public Brand(String name, String logo) {
		this.name = name;
		this.logo = logo;
	}
	
	

   public Brand(String name, String logo, Set<Category> categories) {
		this.name = name;
		this.logo = logo;
		this.categories = categories;
	}


   public Brand(Integer id) {
		this.id = id;
	}
   
   


public Brand(Integer id, String name) {
	this.id = id;
	this.name = name;
}


//getter and setter generation 


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

    ////// toString methot qui qui permet d'imprimer a l'ecran l'objet complet
	@Override
	public String toString() {
		return "Brand [id=" + id + ", name=" + name + ", logo=" + logo + ", categories=" + categories + "]";
	}
	
	@Transient
	public String getImagePath() {
		if(this.id == null) return "/images/thumbnail.png";
		
		return  Constants.S3_BASE_URI + "/brand-logo/"+ this.id +"/"+ this.logo;
	}
	

}
