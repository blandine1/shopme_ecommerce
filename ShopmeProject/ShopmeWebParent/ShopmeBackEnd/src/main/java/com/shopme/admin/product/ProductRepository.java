package com.shopme.admin.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.product.Product;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

	@Query("UPDATE Product p set p.enabled =?2 WHERE p.id =?1")
	@Modifying
	public void updateProductStatus(Integer id, boolean enabled);
	
	public Product findByName(String name);
	
	public Long countById(Integer id);
	
	
	@Query("SELECT p FROM Product p WHERE p.name LIKE %?1%"
			+ "OR p.shortDescription LIKE %?1%"
			+ "OR p.fullDescription LIKE %?1%"
			+ "OR p.brand.name LIKE %?1%"
			+ "OR p.category.name LIKE %?1%")
	public Page<Product> findAll(String keyword, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.category.id=?1 OR p.category.allParentIDs LIKE %?2%")
	public Page<Product> findAllInCtegory(Integer categoryId, String categoryIdMatch, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE (p.category.id=?1 OR p.category.allParentIDs LIKE %?2%) AND "
			+"(p.name LIKE %?3%"
			+" OR p.shortDescription LIKE %?3%"
			+ "OR p.fullDescription LIKE %?3%"
			+ "OR p.brand.name LIKE %?3%"
			+ "OR p.category.name LIKE %?3%)")
	public Page<Product> searchInCategory(Integer categoryId, String categoryIdMatch, String keyword, Pageable pageable);
	
	@Query("SELECT p FROM Product p where p.name like %?1%")
	public Page<Product> searchProductByName(String keyword, Pageable pageable);
	
	
	@Query("update Product p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id= ?1), 0),"
			+ " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id = ?1) WHERE p.id = ?1")
	@Modifying
	public void updateReviewCountAndAverageRating(Integer productId);
}
