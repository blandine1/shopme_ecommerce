package com.shopme.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Brand;

@Service
public class BrandService {
	
	public static final int BRANDS_PER_PAGE = 7;

	@Autowired
	private BrandsRepository repository;
	
	public Iterable<Brand> listAll(){
		return (List<Brand>) repository.findAll();
	}
	
	
	public void listByPage(int pageNum, PagingAndSortingHelper helper){
		helper.listEntities(pageNum, BRANDS_PER_PAGE, repository);
	}
	
	public Brand save(Brand brand) {
		return repository.save(brand);
	}
	
	public Brand get(Integer id) throws BrandNotFoutException {
		try {
			return repository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new BrandNotFoutException("no brand found whit the given id "+id);
		}
	}
	
	public void delete(Integer id) throws BrandNotFoutException {
		Long countById = repository.countById(id);
		if(countById == null || countById == 0) {
			throw new BrandNotFoutException("no brand founded whith the given id "+ id);
		}
		
		repository.deleteById(id);
	}
	
	public String checKUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Brand byName = repository.findByName(name);
		
		if(isCreatingNew){
			if(byName != null) return "Duplicate";
	     }else {
	    	 if(byName != null && byName.getId() != id) {
	    		 return "Duplicate";
	    	 }
	     }
		return "ok";
	}
}
