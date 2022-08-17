package com.shopme.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repotory;
	
	
	@Test
	public void testGetByAlias() {
		String alias = "Apple-iPhone-7-Plus,-32GB,-Jet-Black";
		Product findByAlias = repotory.findByAlias(alias);
		System.out.println("contain of given variable :" + findByAlias.getAlias());
		
		assertThat(findByAlias).isNotNull();
	}
}
