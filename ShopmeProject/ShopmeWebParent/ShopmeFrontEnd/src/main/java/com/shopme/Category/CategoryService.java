package com.shopme.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;
	
	//on recupere tous les tops parents des categories
	public List<Category> listNoChildrenCategories(){
		List<Category> listCategories = new ArrayList<>();
		
		List<Category> listNochildrenCtageories = repository.findAllEnabledCtegory();
		
		listNochildrenCtageories.forEach(category ->{
			Set<Category> children = category.getChildren();
			if(children == null || children.size() == 0) {
				listCategories.add(category);
			}
		});
		
		return listCategories;
	}
	
	public Category getCategory(String alias) throws CategoryNotFoundException{
		
		 Category category = repository.findByAliasEnabled(alias);
		 if(category == null) {
			 throw new CategoryNotFoundException("no category found with the given alias "+ alias);
		 }
		 
		 return category;
		
	}
	
	
	public List<Category> getCategoryParents(Category child){
		List<Category> listParents = new ArrayList<>();
		
		Category parent = child.getParent();
		
		while(parent != null) {
			listParents.add(0, parent);
			parent = parent.getParent();
		}
		
		listParents.add(child);
		
		return listParents;
	}
	
	
}
