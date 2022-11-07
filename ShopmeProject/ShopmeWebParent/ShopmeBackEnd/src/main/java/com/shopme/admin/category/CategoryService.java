package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;

@Service
@Transactional
public class CategoryService {
	
	public static final int ROOT_CATEGORIES_PER_PAGE = 4;

	@Autowired
	private CategoryRepository categoryRepository;

	public List<Category> listByPage(CategoryPageInfo pageInfo ,int pageNum, String sortDir, String keyword) {

		Sort sort = Sort.by("name");
		
		if(sortDir.equals("asc")) {
			sort=sort.ascending();
		}else if(sortDir.equals("desc")) {
			sort = sort.descending();
		}
		
		Pageable pageable =PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);
		
		Page<Category> pageCategories = null;
		
		if(keyword != null && !keyword.isEmpty()) {
			 pageCategories = categoryRepository.search(keyword,pageable);
		}else {
			 pageCategories = categoryRepository.findRootCategories(pageable);
		}
		
		
		List<Category> rootCategories = pageCategories.getContent();
		
		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());
		
		if(keyword != null && !keyword.isEmpty()) {
			List<Category> searchResult = pageCategories.getContent();
			  for(Category c : searchResult) {
				  c.setHasChildren(c.getChildren().size() > 0);
			  }
			  
			  return searchResult;
		}else {
			return listHierarchicalCategories(rootCategories, sortDir); 
		}
		
	}
	
	private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
		List<Category> hierarchicalCategorie = new ArrayList<>();
		
		 for (Category rootCategory : rootCategories) {
			 hierarchicalCategorie.add(Category.copyFull(rootCategory));
			 
			 Set<Category> children = sorteSubCategory(rootCategory.getChildren(), sortDir);
			  
			 for(Category subCategory : children) {
				 String name = "--"+ subCategory.getName();
				 hierarchicalCategorie.add(Category.copyFull(subCategory, name));
				 
				 listSubHierarchicalCategories(hierarchicalCategorie, subCategory, 1, sortDir);
			 }
		 }
		 
		return hierarchicalCategorie;
	}
	
	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,Category parent, int sublevel, String sortDir) {
		int newSublevel = sublevel + 1;
		
		Set<Category> children = sorteSubCategory(parent.getChildren(), sortDir);
		
		for(Category subCategory : children) {
			String name = "";
			for(int i=0; i < newSublevel ; i++) {
				name += "--";
			}
			name += subCategory.getName();
			hierarchicalCategories.add(Category.copyFull(subCategory, name));
			
			listSubHierarchicalCategories(hierarchicalCategories,subCategory, newSublevel, sortDir);
		}
	}

	public Category saveCategory(Category category) {
		Category parent = category.getParent();
		
		if(parent != null) {
			String allParentIDs = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
			allParentIDs += String.valueOf(parent.getId()) + "-";
			category.setAllParentIDs(allParentIDs);
		}
		
		return categoryRepository.save(category);
	}

	public List<Category> listCategoriesUsedInForm() {
		List<Category> listUsedInForm = new ArrayList<>();

		  Iterable<Category> listCategoriesUsedInDb = categoryRepository.findRootCategories(Sort.by("name").ascending());

		  for(Category category : listCategoriesUsedInDb) {
				if( category.getParent() == null) {
				   listUsedInForm.add(Category.copyCategoryIdAndName(category));
				   
				   Set<Category> children = sorteSubCategory(category.getChildren());

				  for(Category subCategory : children) {
					  String name = "--"+ subCategory.getName();
					  listUsedInForm.add(Category.copyCategoryIdAndName(subCategory.getId(), name));
					  listSubCategoriesUsedInForm(listUsedInForm,subCategory, 1);
				}
			 }
			}
		  return listUsedInForm;
	}

	private void listSubCategoriesUsedInForm(List<Category> listUsedInForm,Category parent, int subLevel){
		int newSubLevel = subLevel + 1;

		Set<Category> children = sorteSubCategory(parent.getChildren());

		for(Category subCategory : children) {
			String name = "";
			for(int i=0; i < newSubLevel ; i++) {
				name += "--";
			}
			name += subCategory.getName();
			listUsedInForm.add(Category.copyCategoryIdAndName(subCategory.getId(), name));

			listSubCategoriesUsedInForm(listUsedInForm,subCategory, newSubLevel);
		}
	}
	
	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			 return categoryRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CategoryNotFoundException("category is not found whith the given id : "+id);
		}
	}

	
	public String checkUnique(Integer id, String name, String alias) {
		boolean iscreatingNew  = (id == null || id == 0);
		
		Category categoryByname = categoryRepository.findByName(name);
		
		if(iscreatingNew) {
			if(categoryByname != null) {
				return "DuplicateName";
			}else {
				Category findByAlias = categoryRepository.findByAlias(alias);
				if(findByAlias != null) {
					return "DuplicateAlias";
				}
			}
		}else {
			if(categoryByname != null && categoryByname.getId() != id) {
				return "DuplicateName";
			}else {
				Category findByAlias = categoryRepository.findByAlias(alias);
				if (findByAlias != null && findByAlias.getId() != id) {
					return "DuplicateAlias";
				}
			}
		}
		return "ok";
	}
	
	private SortedSet<Category> sorteSubCategory(Set<Category> children){
		return sorteSubCategory(children,"asc");
	}
	
	private SortedSet<Category> sorteSubCategory(Set<Category> children, String sortDir){
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

			@Override
			public int compare(Category cat1, Category cat2) {
				if(sortDir.equals("asc")){
					return cat1.getName().compareTo(cat2.getName());
				}else{
					return cat2.getName().compareTo(cat1.getName());
				}
				
			}
		});
		
		sortedChildren.addAll(children);
		
		return sortedChildren;
	}
	
	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		categoryRepository.updateCateStatus(id, enabled);
	}
	
	public void deleteCategory(Integer id) throws CategoryNotFoundException {
	  Long countbyId = categoryRepository.countById(id);
	  if(countbyId == 0 || countbyId == null) {
		  throw new CategoryNotFoundException("could not found any category whith the given ID :"+id);
	  }
	   categoryRepository.deleteById(id);
	}
}
