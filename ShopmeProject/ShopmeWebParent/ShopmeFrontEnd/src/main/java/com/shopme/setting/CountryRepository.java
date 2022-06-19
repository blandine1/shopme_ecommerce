package com.shopme.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Country;

public interface CountryRepository extends CrudRepository<Country, Integer> {

	//retourne la liste des Contry trier par nom dans l'ordre crossant
	public List<Country> findAllByOrderByNameAsc();
}
