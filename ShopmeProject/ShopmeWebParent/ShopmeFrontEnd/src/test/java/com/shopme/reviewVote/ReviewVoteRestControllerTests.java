package com.shopme.reviewVote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.Review;
import com.shopme.review.ReviewRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewVoteRestControllerTests {
	
	@Autowired private ReviewRepository reviewRepository;
	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;
	
	@Test
	public void testNotLogin() throws Exception {
		String requestUrl = "/vote_review/1/up";
		
		MvcResult mvcResult = mockMvc.perform(post(requestUrl).with(csrf()))
		   .andExpect(status().isOk())
		   .andDo(print())
		   .andReturn();
		
		String json = mvcResult.getResponse().getContentAsString();
		VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
		
		assertThat(voteResult.getMessage()).contains("You must login");
	}
	
	@Test
	@WithMockUser(username = "tina.jamerson@gmail.com", password = "tina2020")
	public void testVoteNonExistingReview() throws Exception {
		String requestUrl = "/vote_review/123/up";
		
		MvcResult mvcResult = mockMvc.perform(post(requestUrl).with(csrf()))
		   .andExpect(status().isOk())
		   .andDo(print())
		   .andReturn();
		
		String json = mvcResult.getResponse().getContentAsString();
		VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
		
		assertThat(voteResult.getMessage()).contains("no longer exist");
	}
	
	@Test
	@WithMockUser(username = "tina.jamerson@gmail.com", password = "tina2020")
	public void testVoteUp() throws Exception {
		
		int reviewId = 10;
		String requestUrl = "/vote_review/"+ reviewId +"/up";
		
		Review review = reviewRepository.findById(reviewId).get();
		int votecountBefor = review.getVotes();
		
		MvcResult mvcResult = mockMvc.perform(post(requestUrl).with(csrf()))
		   .andExpect(status().isOk())
		   .andDo(print())
		   .andReturn();
		
		String json = mvcResult.getResponse().getContentAsString();
		VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);
		
		assertThat(voteResult.getMessage()).contains("You have successfully voted up");
		
		int voteCoutAfter = voteResult.getVoteCount();
		
	}


}
