package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {
	
	@MockBean
	private CategoryRepository repository;
	
	@InjectMocks
	private CategoryService service;
	
	@Test
	public void testCheckUniqueInNewModeAndRetunDuplicateName() {
		Integer id = null;
		String name= "Computers";
		String alias = "abc";
		
		Category cat = new Category(id, name, alias);
		
		Mockito.when(repository.findByName(name)).thenReturn(cat);
		Mockito.when(repository.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateName");
	}
	
	@Test
	public void testCheckUniqueInNewModeAndRetunDuplicateAlias() {
		Integer id = null;
		String name= "absc";
		String alias = "Computers";
		
		Category cat = new Category(id, name, alias);
		
		Mockito.when(repository.findByName(name)).thenReturn(null);
		Mockito.when(repository.findByAlias(alias)).thenReturn(cat);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateAlias");
	}

	@Test
	public void testCheckUniqueInNewModeAndRetunOk() {
		Integer id = null;
		String name= "Clothe";
		String alias = "clothe";
		
		
		Mockito.when(repository.findByName(name)).thenReturn(null);
		Mockito.when(repository.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("ok");
	}
	
	@Test
	public void testCheckUniqueInEditModeAndRetunDuplicateName() {
		Integer id = 1;
		String name= "Computers";
		String alias = "abc";
		
		Category cat = new Category(17, name, alias);
		
		Mockito.when(repository.findByName(name)).thenReturn(cat);
		Mockito.when(repository.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateName");
	}
	
	@Test
	public void testCheckUniqueInEditModeAndRetunDuplicateAlias() {
		Integer id = 1;
		String name= "absc";
		String alias = "Computers";
		
		Category cat = new Category(2, name, alias);
		
		Mockito.when(repository.findByName(name)).thenReturn(null);
		Mockito.when(repository.findByAlias(alias)).thenReturn(cat);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateAlias");
	}
	
	@Test
	public void testCheckUniqueInEditModeAndRetunOk() {
		Integer id = 1;
		String name= "absc";
		String alias = "Computers";
		
		Category cat = new Category(id, name, alias);
		
		Mockito.when(repository.findByName(name)).thenReturn(null);
		Mockito.when(repository.findByAlias(alias)).thenReturn(cat);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("ok");
	}
}
