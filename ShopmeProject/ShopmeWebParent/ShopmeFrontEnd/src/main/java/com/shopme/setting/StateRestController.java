package com.shopme.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import com.shopme.common.entity.StateDTO;

@RestController
public class StateRestController {

	@Autowired
	private StateRepository repository;
	
	@GetMapping("/settings/list_states_by_country/{id}")
	public List<StateDTO> listByCountry(@PathVariable(name = "id") Integer countryId){
		 List<StateDTO> dts = new ArrayList<>();
		 List<State> states = repository.findByCountryOrderByNameAsc(new Country(countryId));
		 
		 for(State state : states) {
			 dts.add(new StateDTO(state.getId(), state.getName()));
		 }
		 
		return dts;
	}

}
