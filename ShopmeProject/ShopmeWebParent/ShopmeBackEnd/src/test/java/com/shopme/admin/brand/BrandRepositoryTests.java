package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.category.CategoryRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {

	
	@Autowired
	private BrandsRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	

	@Test
	public void testAddCategoryToBrand() {
		Category categoryId = categoryRepository.findById(6).get();
		
		Brand brand = repository.findById(1).get();
		brand.getCategories().add(categoryId);
		
		Brand saveBrand = repository.save(brand);
		
		assertThat(saveBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateBrand2() {
		/*
		 * Category tablets = categoryRepository.findById(7).get(); Category cleePhone =
		 * categoryRepository.findById(4).get();
		 */
		
		Category tablets = new Category(7);
		Category cleePhone = new Category(4);
		
		Brand apple = new Brand("Apple", "apple.png");
		apple.getCategories().add(tablets);
		apple.getCategories().add(cleePhone);
		
		Brand saveBrand = repository.save(apple);
		
		//System.out.println(saveBrand.getName());
		
		assertThat(saveBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateBrandSamsung() {
		Brand samsung = new Brand("Samsung", "samsung.png");
		
		samsung.getCategories().add(new Category(29));
		samsung.getCategories().add(new Category(24));
		
		Brand saveBrand = repository.save(samsung);
		
		assertThat(saveBrand.getId()).isNotNull();
	}
	
	@Test
	public void getBrandList() {
		Iterable<Brand> listBrands = repository.findAll();
		listBrands.forEach(System.out::println);
	}
	
	@Test
	public void testGetById() {
		Brand brand = repository.findById(9).get();
		System.out.println(brand);
		
		assertThat(brand.getId()).isNotNull();
	}
	
	@Test
	public void testUpdateBrand() {
		String apple = "Apple";
		Brand brand = repository.findById(9).get();
		brand.setName(apple);
		
		Brand savebrand = repository.save(brand);
		assertThat(savebrand.getName()).isEqualTo("apple");
	}
	
	@Test
	public void testAddBrands() {
		
		Brand brand = repository.findById(3).get();
		brand.getCategories().add(new Category(5));
		brand.getCategories().add(new Category(6));
		
		Brand savebrand = repository.save(brand);
		
		assertThat(savebrand.getId()).isNotNull();
	}
	
	@Test
	public void testDeleteBrand() {
		int id = 3;
		  repository.deleteById(id);
	}
	
	@Test
	public void testListByPage() {
		Integer pageNumber = 0;
		Integer pageSize = 4;
        
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<Brand> page = repository.findAll(pageable);
		
		List<Brand> brands = page.getContent();
		
		brands.forEach(b -> System.out.println(b.getName()));
		
		assertThat(brands).size().isGreaterThan(0);
		
	}
	
	
}
