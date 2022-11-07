package com.shopme.admin.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.Country;

@SpringBootTest //test d'integration
@AutoConfigureMockMvc
public class CountryRestControllerTests {

	
	@Autowired
	 MockMvc mockMvc;
	
	@Autowired
	 ObjectMapper mapper;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Test
	@WithMockUser(username= "nam@codejava.net", password = "java2020", roles = "ADMIN")
	public void testGetListCountry() throws Exception {
		String url = "/countries/list";
		
		MvcResult andReturn = mockMvc.perform(get(url))
		   .andExpect(status()
				   .isOk())
		   .andDo(print()).andReturn();
		
		String contentAsString = andReturn.getResponse().getContentAsString();
		
		Country[] countries = mapper.readValue(contentAsString, Country[].class);
		
		
		assertThat(countries).hasSizeGreaterThan(0);
	}
	
	@Test
	@WithMockUser(username= "nam@codejava.net", password = "java2020", roles = "ADMIN")
	public void testCreateCountry() throws JsonProcessingException, Exception {
		String url = "/countries/save";
		String countryName = "ESPAGNE";
		String code = "ES";
		Country country = new Country(countryName, code);
		
		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				.content(mapper.writeValueAsString(country))
				.with(csrf()))
		        .andDo(print())
		        .andExpect(status().isOk()).andReturn();
		
		String asString = result.getResponse().getContentAsString();
		
		Integer countryId = Integer.parseInt(asString);
		
		 Country findById = countryRepository.findById(countryId).get();
		 
		 assertThat(findById.getName()).isEqualTo(countryName);
	}
	
	
	@Test
	@WithMockUser(username= "nam@codejava.net", password = "java2020", roles = "ADMIN")
	public void testUpdateCountry() throws JsonProcessingException, Exception {
		String url = "/countries/save";
		
		Integer countryId = 7;
		String countryName = "Canada";
		String code = "CA";
		Country country = new Country(countryId,countryName, code);
		
		      mockMvc.perform(post(url).contentType("application/json")
				.content(mapper.writeValueAsString(country))
				.with(csrf()))
		        .andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(content().string(String.valueOf(countryId)));
		
		
		 Country findById = countryRepository.findById(countryId).get();
		 
		 assertThat(findById.getName()).isEqualTo(countryName);
	}
	
	@Test
	@WithMockUser(username= "nam@codejava.net", password = "java2020", roles = "ADMIN")
	public void testDeleteCountry() throws Exception {
		Integer id = 8;
		String url = "/countries/delete/"+ id;
		
        mockMvc.perform(get(url)).andExpect(status().isOk());
        
        Optional<Country> findById = countryRepository.findById(id);
        
        assertThat(findById.isEmpty());
	}
	
	
}
