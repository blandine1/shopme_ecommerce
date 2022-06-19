package com.shopme.admin.state;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer> {

	//retourne la list des state by specific contry
	public List<State> findByCountryOrderByNameAsc(Country country);
}
