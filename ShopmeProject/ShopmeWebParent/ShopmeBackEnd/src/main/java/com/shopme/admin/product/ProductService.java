package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFountException;

@Service
@Transactional
public class ProductService {
	public  static final int PRODUCT_PER_PAGE = 5;
	@Autowired
	private ProductRepository repository;
	
	public List<Product> listAll() {
		return (List<Product>) repository.findAll();
	}
	
public void listByPage(int pageNum, PagingAndSortingHelper helper, Integer categoryId) {
		
		Pageable pageable = helper.createPageable(pageNum,PRODUCT_PER_PAGE);
		String keyword = helper.getKeyword();
		Page<Product> page = null;

		if (keyword != null && !keyword.isEmpty()) {
			
			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				page = repository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
			} else {
				page = repository.findAll(keyword, pageable);
			}
		} else {
			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				page = repository.findAllInCtegory(categoryId, categoryIdMatch, pageable);
			} else {		
				page = repository.findAll(pageable);
			}
		}
		
		helper.updateModelAttributes(pageNum, page);
	}
	
	public void searchProducts(int pageNum, PagingAndSortingHelper helper) {
		Pageable pageable = helper.createPageable(pageNum, PRODUCT_PER_PAGE);
		String keyword = helper.getKeyword();
		
		Page<Product> page = repository.searchProductByName(keyword, pageable);
		
		helper.updateModelAttributes(pageNum, page);
	}
	
	public Product save(Product product) {
		
		if(product.getId() == null) {
			product.setCreatedTime(new Date());
		}
		
		if(product.getId() == null || product.getAlias().isEmpty()) {
			String aliasNew = product.getName().replace(" ", "-");
			product.setAlias(aliasNew);
		}else {
			product.setAlias(product.getAlias().replace(" ", "-"));
		}
		
		product.setUpdatedTime(new Date());
		
		 Product updatedProduct = repository.save(product);
		 repository.updateReviewCountAndAverageRating(updatedProduct.getId());
		 
		 return updatedProduct;
	}
	
	public void saveProductPrice(Product productInform) {
		Product productInDb = repository.findById(productInform.getId()).get();
		productInDb.setCost(productInform.getCost());
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
