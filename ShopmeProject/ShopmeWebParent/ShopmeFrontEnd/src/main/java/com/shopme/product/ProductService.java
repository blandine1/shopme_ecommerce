package com.shopme.product;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFountException;

@Service
public class ProductService {

	public static final int PRODUCT_PER_PAGE = 10;
	public static final int SEARCH_RESULT_PER_PAGE = 10;
	
	@Autowired 
	private ProductRepository productRepository;
	
	public Page<Product> listByCategory(int pageNum, Integer categoryId){
		
		String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE);
		
		return productRepository.listByCategory(categoryId, categoryIdMatch, pageable);
	}
	
	public Product findProductByAlias(String alias) throws ProductNotFountException {
		
		 Product product = productRepository.findByAlias(alias);
		  if(product == null) {
			  throw new ProductNotFountException("could not found product with the given alias "+ alias);
		  }
		return product;
	}
	
	public Page<Product> search(String keyword, int pageNum){
		Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULT_PER_PAGE);
		
		return productRepository.search(keyword, pageable);
	}
}
