package com.shopme.admin.brand;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Brand;


@Repository
public interface BrandsRepository extends PagingAndSortingRepository<Brand, Integer> {
   //cout est utiliser seulement dans le cadre de la suppression du message
    
	public Long countById(Integer id);
	
    public Brand findByName(String name);
    
	@Query("SELECT b from Brand b where b.name like %?1%")
	public Page<Brand> findAll(String keyword, Pageable pageable);

	@Query("SELECT NEW Brand(b.id, b.name) from Brand b ORDER BY b.name ASC")
	public List<Brand> findAll();
}
