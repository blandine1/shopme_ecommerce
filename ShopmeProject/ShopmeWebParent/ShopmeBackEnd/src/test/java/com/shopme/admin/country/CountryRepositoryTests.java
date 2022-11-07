package com.shopme.admin.country;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryRepositoryTests {

	@Autowired
	private CountryRepository countryRepository;
	
	@Test
	public void tsetCreateFirstCountry() {
		
		Country c2= new Country("ANGLETERRE", "AGL");
		Country c3= new Country("ALLEMAGNE", "ALL");
		Country c4= new Country("CAMEROUN", "CM");
		
		Iterable<Country> saveAll = countryRepository.saveAll(List.of(c2, c3, c4));
		
		assertThat(saveAll).size().isGreaterThan(0);
	}
	
	@Test
	public void testGetSpecificCountry() {
		Country country = countryRepository.findById(4).get();
		
		System.out.println(country);
		
		assertThat(country).isNotNull();
	}
	
	@Test
	public void getListOfCountry() {
		Iterable<Country> findAll = countryRepository.findAll();
		
		findAll.forEach(System.out :: println);
	}
	
	
	@Test
	public void testUpdateCountry() {
		Country country = countryRepository.findById(4).get();
		country.setName("Angletterre");
		country.setCode("agl");
		
		countryRepository.save(country);
	}
	
	@Test
	public void testDeleteCountry() {
		countryRepository.deleteById(4);
	    Optional<Country> findById = countryRepository.findById(4);
		assertThat(findById.isEmpty());
	}
}
