package com.shopme.admin.country;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Country;

@RestController
public class CountryRestController {

	@Autowired
	private CountryRepository countryRepository;
	
	@GetMapping("/countries/list")
	public List<Country> listAll(){
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	@PostMapping("/countries/save")
	public String save(@RequestBody Country country) {
		Country savedCountry = countryRepository.save(country);
		return String.valueOf(savedCountry.getId());
	}
	
	@DeleteMapping("/countries/delete/{id}")
	public void deleteCountries(@PathVariable("id") Integer id) {
		countryRepository.deleteById(id);
	}
}
