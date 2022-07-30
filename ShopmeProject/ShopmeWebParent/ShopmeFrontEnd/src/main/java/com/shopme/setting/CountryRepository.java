package com.shopme.setting;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Country;

public interface CountryRepository extends CrudRepository<Country, Integer> {

	//retourne la liste des Contry trier par nom dans l'ordre crossant
	public List<Country> findAllByOrderByNameAsc();
	
	@Query("SELECT c from Country c where c.code = ?1")
	public Country findByCode(String code);
}
