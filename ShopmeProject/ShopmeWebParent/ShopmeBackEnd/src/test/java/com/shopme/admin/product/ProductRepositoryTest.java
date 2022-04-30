package com.shopme.admin.product;

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

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {

	
	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private TestEntityManager manager;  
	
	@Test
	public void CreateProduct(){
		
		Brand brand = manager.find(Brand.class, 37);
		Category category = manager.find(Category.class, 5);
		
		Product product = new Product();
		product.setName("Acer Aspired DeskTop");
		product.setAlias("Acer_Aspired");
		product.setShortDescription("The first Acer product");
		product.setFullDescription("this is full description for Acer Aspired");
		
		product.setPrice(700);  
		product.setCost(600);
		product.setInStock(true);
		product.setEnabled(true);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		product.setBrand(brand);
		product.setCategory(category);
		
		Product savedProduct = repository.save(product);
		
		assertThat(savedProduct.getId()).isNotNull();
	}
	
	@Test
	public void testGetAllProduct() {
		Iterable<Product> listProducts = repository.findAll();
		
		listProducts.forEach(System.out :: println);
	}
	
	@Test
	public void GetSingleProduct(){
		Integer id = 1;
		Optional<Product> findById = repository.findById(id);
		System.out.println(findById);
		
		assertThat(findById).isNotNull();
	}
	
	@Test
	public void testUpdateProduct() {
		Product product = repository.findById(2).get();
		String name = "Dell Inspiron 3000";
		
		product.setName(name);
		
		Product saveProduct = repository.save(product);
		
		assertThat(saveProduct.getName()).isEqualTo("Dell Inspiron 3000");
	}
	
	@Test
	public void testDelete() {
		Integer id = 4;
		
		repository.deleteById(id);
		
		Product p = manager.find(Product.class, id);
		
		assertThat(p.getId()).isNull();
	}
	
	
}
