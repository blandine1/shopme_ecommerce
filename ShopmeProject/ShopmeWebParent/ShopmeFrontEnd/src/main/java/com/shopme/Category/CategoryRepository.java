package com.shopme.Category;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Category;

public interface CategoryRepository extends CrudRepository<Category, Integer>{

	@Query("SELECT c FROM Category c WHERE c.enabled = true ORDER by c.name ASC")
	public List<Category> findAllEnabledCtegory();
	
	@Query("SELECT c FROM Category c WHERE c.enabled = true AND c.alias = ?1")
	public Category findByAliasEnabled(String alias);
}
