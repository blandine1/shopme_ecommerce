package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {

	@Autowired
	private CategoryRepository repository;

	@Test
	public void testCreateFirstRootCategory() {

		Category category = new Category("Electronics");
		Category savedCategory = repository.save(category);

		assertThat(savedCategory.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateSubCategory() {
		Category parent = new Category(7);
		Category subCategory = new Category("Iphone", parent);

		 Category saveSubCategory = repository.save(subCategory);

		assertThat(saveSubCategory.getId()).isNotNull();
	}

	@Test
	public void testGetCategory() {
		Category category = repository.findById(1).get();
		System.out.println(" - "+category.getName());

		Set<Category> children = category.getChildren();

		for(Category subCategory : children) {
			 System.out.println("-- :"+subCategory.getName());

			 Set<Category> children2 = subCategory.getChildren();

			 for(Category petitFils : children2) {
				 System.out.println(" --- " +petitFils.getName());
			 }
		}

		assertThat(children.size()).isGreaterThan(0);
	}

	@Test
	public void testPrintHierrachicalCategory() {
		Iterable<Category> catgories = repository.findAll();

		for(Category category : catgories) {
			if( category.getParent() == null) {
				System.out.println(category.getName());

			Set<Category> children = category.getChildren();

			for(Category subCategory : children) {
				System.out.println("-- "+ subCategory.getName());

				printChildren(subCategory, 1);
			}
		 }
		}

		assertThat(catgories).size().isGreaterThan(0);
	}

	private void printChildren(Category parent, int subLevel){
		int newSubLevel = subLevel + 1;

		Set<Category> children = parent.getChildren();

		for(Category subCategory : children) {
			for(int i=0; i < newSubLevel ; i++) {
				System.out.print("--");
			}
			System.out.println(subCategory.getName());

			printChildren(subCategory, newSubLevel);
		}
	}
	
	@Test
	public void testGetRootCtageories() {
		List<Category> listRootCategories = repository.findRootCategories(Sort.by("name").ascending());
		
		for(Category c : listRootCategories){
			System.out.println("parent : "+c.getName());
		}
		
		assertThat(listRootCategories.size()).isGreaterThan(0);
	}
	
	
	@Test
	public void testGetCatName() {
		String name = "Computers";
		Category findByName = repository.findByName(name);
		
		assertThat(findByName).isNotNull();
	}
	
	@Test
	public void testEnabledCategory() {
		Integer id = 14;
	    repository.updateCateStatus(id, true);
	}

}
