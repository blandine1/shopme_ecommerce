package com.shopme.order;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderRestControllerTests {
	
	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;
	
	@Test
	@WithUserDetails("luctandoum@gmail.com")
	public void testSendOrderReturnedREquestFailed() throws  Exception {
		Integer orderId = 102;
		OrderReturnedRequest returnedRequest = new OrderReturnedRequest(orderId, "","");
		
		String requestUrl = "/orders/return";
		
		mockMvc.perform(post(requestUrl)
				  .with(csrf())
				  .contentType("application/json")
				  .content(objectMapper.writeValueAsString(returnedRequest)))
		.andExpect(status().isNotFound())
		.andDo(print());
	}
	
	@Test
	@WithUserDetails("luctandoum@gmail.com")
	public void testSendOrderReturnedREquestSuccessfull() throws  Exception {
		Integer orderId = 15;
		String reason = "j'ai eu un muavais produit";
		String note = "bien vouloir me rembourser";
		OrderReturnedRequest returnedRequest = new OrderReturnedRequest(orderId, reason, note);
		
		String requestUrl = "/orders/return";
		
		mockMvc.perform(post(requestUrl)
				  .with(csrf())
				  .contentType("application/json")
				  .content(objectMapper.writeValueAsString(returnedRequest)))
		.andExpect(status().isOk())
		.andDo(print());
	}

}
