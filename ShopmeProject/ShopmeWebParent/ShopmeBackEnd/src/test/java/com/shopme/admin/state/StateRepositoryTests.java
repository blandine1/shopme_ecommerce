package com.shopme.admin.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class StateRepositoryTests {

	@Autowired
	private StateRepository stateRepository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateStateIndia() {
		//Integer contryId = 6;
		Country countryId = entityManager.find(Country.class, 6);
		State state1 = new State("DOUALA", countryId);
		State state2 = new State("BAFOUSSAM", countryId);
		State state3 = new State("YAOUNDE", countryId);
		State state4 = new State("EBOLOWA", countryId);
		//State state1 = stateRepository.save(new State("DOUALA", countryId));
		//State state2 = stateRepository.save(new State("BAFOUSSAM", countryId));
		//State state3 = stateRepository.save(new State("YAOUNDE", countryId));
		//State state4 = stateRepository.save(new State("EBOLOWA", countryId));
		
		Iterable<State> saveAll = stateRepository.saveAll(List.of(state1, state2, state3, state4));
		
		assertThat(saveAll).size().isGreaterThan(0);
	}
	
	@Test
	public void testCreateStateInUSA() {
		Country countryId = entityManager.find(Country.class, 1);
		
		State state1 = new State("CALIFORNIA", countryId);
		State state2 = new State("TEXAS", countryId);
		State state3 = new State("NEW YOK", countryId);
		State state4 = new State("WASHINGTON", countryId);
		
		Iterable<State> saveAll = stateRepository.saveAll(List.of(state1, state2, state3, state4));
		
		assertThat(saveAll).size().isGreaterThan(0);
	}
	
	@Test
	public void testlistState() {
		Iterable<State> findAll = stateRepository.findAll();
		
		findAll.forEach(c -> System.out.println(c.getName()));
		
		assertThat(findAll).size().isGreaterThan(0);
	}
	
	@Test
	public void testGetStateByCountry() {
		
		Country country2 = entityManager.find(Country.class, 6);
		List<State> byNameAsc = stateRepository.findByCountryOrderByNameAsc(country2);
		
		byNameAsc.forEach(s -> System.out.println(s.getName()));
	}
	
	@Test
	public void getState() {
		State findById = stateRepository.findById(4).get();
		
		System.out.println(findById.getName());
		
		assertThat(findById).isNotNull();
	}
	
	@Test
	public void updateState() {
		State findById = stateRepository.findById(4).get();
		findById.setName("BERTOUA");
		
		System.out.println(findById.getName());
		
		assertThat(findById).isNotNull();
	}
 	
	@Test
	public void deleteState() {
		stateRepository.deleteById(4);
		
		Optional<State> findById = stateRepository.findById(4);
		assertThat(findById.isEmpty());
		
	}
 	
	
}
