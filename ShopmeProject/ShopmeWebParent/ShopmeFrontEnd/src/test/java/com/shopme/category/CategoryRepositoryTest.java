package com.shopme.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.Category.CategoryRepository;
import com.shopme.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
public class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository repository;
	 
	@Test
	public void testGetAllEnabledCategory() {
		List<Category> listEnabled = repository.findAllEnabledCtegory();
		
		listEnabled.forEach(c -> {
			System.out.println(c.getName() +" ( "+ c.isEnabled()+" )");
		});
		
		int size = listEnabled.size();
		System.out.println("la taille des elements est :"+size);
	}
	
	@Test
	public void testFindCategoryByALias() {
		String alias = "cellphone_accessories";
		
		Category findByAlias = repository.findByAliasEnabled(alias);
		
		System.out.println("alias "+findByAlias.getName());
		
		assertThat(findByAlias.getAlias()).isEqualTo(alias);
	}
}
