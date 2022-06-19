package com.shopme.admin.state;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.server.PathParam;

import org.apache.commons.compress.archivers.zip.X7875_NewUnix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import com.shopme.common.entity.StateDTO;

@RestController
public class StateRestController {

	@Autowired
	private StateRepository repository;
	
	@GetMapping("/states/list_by_country/{id}")
	public List<StateDTO> listByCountry(@PathVariable(name = "id") Integer countryId){
		 List<StateDTO> dts = new ArrayList<>();
		 List<State> states = repository.findByCountryOrderByNameAsc(new Country(countryId));
		 
		 for(State state : states) {
			 dts.add(new StateDTO(state.getId(), state.getName()));
		 }
		 
		return dts;
	}
	
	@PostMapping("/states/save")
	public String save(@RequestBody State state) {
		 State save = repository.save(state);
		return String.valueOf(save.getId());
	}
	
	@DeleteMapping("/states/delete/{id}")
	public void delete(@PathVariable(name = "id") Integer id) {
		     repository.deleteById(id);
	}
}
