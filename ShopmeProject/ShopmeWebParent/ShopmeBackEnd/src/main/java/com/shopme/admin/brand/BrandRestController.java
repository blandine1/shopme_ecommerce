package com.shopme.admin.brand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@RestController
public class BrandRestController {
	
	@Autowired
	private BrandService service;

	@PostMapping("/brand/checkUnique")
	public String checkUnique(@Param("id") Integer id, @Param("name") String name) {
		return service.checKUnique(id, name);
	}
	
	@GetMapping("/brands/{id}/categories")
	public List<CategoryDTO> listCategoriesByBrandId(@PathVariable(name = "id") Integer id) throws BrandNotFoudRestException {
		List<CategoryDTO> listCategories = new ArrayList<>();
		try {
			Brand brand = service.get(id);
			Set<Category> categories = brand.getCategories();
			for(Category category: categories) {
				CategoryDTO dto= new CategoryDTO(category.getId(), category.getName());
				listCategories.add(dto);
			}
			return listCategories;
		} catch (BrandNotFoutException e) {
			throw new BrandNotFoudRestException();
		}
	}
}
