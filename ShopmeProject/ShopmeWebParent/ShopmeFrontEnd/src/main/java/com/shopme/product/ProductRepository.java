package com.shopme.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.product.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>{

	//on retourne tous les produits appartenant a une certaine categorie
	@Query("SELECT p FROM Product p WHERE p.enabled = true AND (p.category.id = ?1 or p.category.allParentIDs like %?2%)"
			+ "ORDER by p.name ASC")
	public Page<Product> listByCategory(Integer id, String categoryIdMAtch, Pageable pageable);
	
	public Product findByAlias(String alias); 
	
	//cette reqiuette n'est pas celle de JPA mais de MYSQL
	@Query(value = "SELECT * FROM products  where enabled=true AND MATCH(name, short_description, full_description) AGAINST (?1)",
			nativeQuery = true)
	public Page<Product> search(String keyword, Pageable pageable);
	
	@Query("update Product p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id= ?1), 0),"
			+ " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id = ?1) WHERE p.id = ?1")
	@Modifying
	public void updateReviewCountAndAverageRating(Integer productId);
}
