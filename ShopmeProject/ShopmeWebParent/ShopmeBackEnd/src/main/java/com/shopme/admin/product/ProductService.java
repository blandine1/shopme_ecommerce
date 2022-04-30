package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFountException;

@Service
@Transactional
public class ProductService {
	public final static int PRODUCT_PER_PAGE = 5;
	@Autowired
	private ProductRepository repository;
	
	public List<Product> listAll() {
		return (List<Product>) repository.findAll();
	}
	
	public Page<Product> listByPage(int pageNum, String sortField,String sortDir ,String keyword, Integer categoryId){
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum -1,PRODUCT_PER_PAGE, sort);
		
		if(keyword != null && !keyword.isEmpty()){
			if(categoryId != null && categoryId > 0) {
				String categoryMatch = "-"+String.valueOf(categoryId).concat("-");
				return repository.searchInCategory(categoryId, categoryMatch, keyword, pageable);
			}
			return repository.findAll(keyword ,pageable);
		}
		
		if(categoryId != null && categoryId > 0) {
			String categoryMatch = "-"+String.valueOf(categoryId).concat("-");
			return repository.findAllInCtegory(categoryId, categoryMatch, pageable);
		}
		
		return repository.findAll(pageable);
	}
	
	public Product save(Product product) {
		product.setCreatedTime(new Date());
		
		if(product.getId() == null || product.getAlias().isEmpty()) {
			String aliasNew = product.getName().replace(" ", "-");
			product.setAlias(aliasNew);
		}else {
			product.setAlias(product.getAlias().replace(" ", "-"));
		}
		
		product.setUpdatedTime(new Date());
		
		return repository.save(product);
	}
	
	public void saveProductPrice(Product productInform) {
		Product productInDb = repository.findById(productInform.getId()).get();
		productInDb.setCost(productInform.getId());
		productInDb.setPrice(productInform.getPrice());
		productInDb.setDiscountPercent(productInform.getDiscountPercent());
		
		repository.save(productInDb);
	}
	
	public String checkUnique(Integer id,String name) {
	       boolean iscreatingNew =(id == null || id ==0);
	       Product findByName = repository.findByName(name);
	       
	       if(iscreatingNew) {
	    	   if(findByName != null) 
	    		   return "Duplicate";
	       }else {
	    	   if(findByName != null && findByName.getId() != id) {
	    		   return "Duplicate";
	    	   }
	       }
	       
	       return "ok";
	}
	
	public void updateProductSatus(Integer id, boolean enabled) {
		repository.updateProductStatus(id, enabled);
	}
	
	public void deleteProduct(Integer id) throws ProductNotFountException {
		Long countById = repository.countById(id);
		
		if(countById == null || countById == 0) {
			throw new ProductNotFountException("coult not found any product with id "+id);
		}
		
		repository.deleteById(id);
		
	}
	
	public Product get(Integer id) throws ProductNotFountException {
		try {
			return repository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw  new ProductNotFountException("could not found any product with the given ID "+ id);
		}
	}

}
