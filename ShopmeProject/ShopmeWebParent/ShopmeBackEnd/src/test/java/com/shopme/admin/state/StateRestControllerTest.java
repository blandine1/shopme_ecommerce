package com.shopme.admin.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTest {

	@Autowired
	StateRepository stateRepository;
	
	@Autowired
    ObjectMapper mapper;
	
	@Autowired
    MockMvc mockMvc;
	
	@Autowired
    CountryRepository countryRepository;
	
	
	@Test
	@WithMockUser(username = "nam", password = "lucsonore", roles = "Admin")
	public void testStateByCountry() throws Exception {
		Integer countryId = 6;
		String url = "/states/list_by_country/"+ countryId;
		
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk())
		.andDo(print())
		.andReturn();
		
		String jsonResponse = result.getResponse().getContentAsString();
		State[] states = mapper.readValue(jsonResponse, State[].class);
		
		assertThat(states).hasSizeGreaterThan(1);
		
	}
	
	

	@Test
	@WithMockUser(username = "nam", password = "lucsonore", roles = "Admin")
	public void testCreateState() throws JsonProcessingException, Exception {
		
		Country countryId = countryRepository.findById(6).get();
		
		String st1 = "BUEA";
		String st2 = "Bangangte";
		
		String url = "/states/save";
		
		State state = new State(st1, countryId);
		
		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				.content(mapper.writeValueAsString(state)).with(csrf())).andExpect(status().isOk())
		        .andDo(print())
		        .andReturn();
		
		String asString = result.getResponse().getContentAsString();
		Integer id = Integer.parseInt(asString);
		Optional<State> findById = stateRepository.findById(id);
		
		assertThat(findById.isPresent());
	}
	
	
	@Test
	@WithMockUser(username = "nam", password = "lucsonore", roles = "Admin")
	public void update() throws JsonProcessingException, Exception {
		
		String url = "/states/save";
		Integer id = 6;
		State byId = stateRepository.findById(id).get();
		
		byId.setName("buea");
		
		MvcResult result = mockMvc.perform(post(url).contentType("application/json").content(mapper.writeValueAsString(byId)).with(csrf()))
		   .andExpect(status().isOk())
		   .andDo(print())
		   .andReturn();
		
		String data = result.getResponse().getContentAsString();
		
		//State[] states = mapper.readValue(data, State[].class);
		Integer savedId = Integer.parseInt(data);
		//System.out.println("//////////////////////////////// "+savedId);
		
		assertThat(savedId).isEqualTo(id);
		
	}
	
	
	@Test
	@WithMockUser(username = "nam", password = "lucsonore", roles = "Admin")
	public void delete() throws Exception{
		Integer id = 9;
		String url ="/states/delete/"+ id;
		
		mockMvc.perform(get(url)).andExpect(status().isOk());
		
		Optional<State> findById = stateRepository.findById(id);
		
		assertThat(findById.isEmpty());
	}
	
	
	
	
}
