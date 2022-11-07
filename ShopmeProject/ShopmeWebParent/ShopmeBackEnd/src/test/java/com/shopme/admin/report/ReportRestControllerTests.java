package com.shopme.admin.report;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportRestControllerTests {

	
	@Autowired private MockMvc mockMvc;
	
	@Test
	@WithMockUser(username = "uers2", password = "pass2", authorities = {"Salesperson"})
	public void testGetReportLast7Days() throws Exception {
		String requestUrl = "/reports/sales_by_date/last_7_days";
		
		mockMvc.perform(get(requestUrl)).andExpect(status().isOk()).andDo(print());
	}
	
	@Test
	@WithMockUser(username = "uers2", password = "pass2", authorities = {"Salesperson"})
	public void testGetReportLast6Months() throws Exception {
		String requestUrl = "/reports/sales_by_date/last_6_months";
		
		mockMvc.perform(get(requestUrl)).andExpect(status().isOk()).andDo(print());
	}
	
	@Test
	@WithMockUser(username = "uers2", password = "pass2", authorities = {"Salesperson"})
	public void testGetReportByDataRange() throws Exception {
		
		String startDate = "2022-09-01";
		String endDate = "2022-09-30";
		String requestUrl = "/reports/sales_by_date/" + startDate + "/" + endDate;
		
		mockMvc.perform(get(requestUrl)).andExpect(status().isOk()).andDo(print());
	}
	
	@Test
	@WithMockUser(username = "uers2", password = "pass2", authorities = {"Salesperson"})
	public void testGetReportByCategory() throws Exception {
		
	      String requestUrl = "/reports/category/last_7_days";
		
	      mockMvc.perform(get(requestUrl)).andExpect(status().isOk()).andDo(print());
	}
	
	@Test
	@WithMockUser(username = "uers2", password = "pass2", authorities = {"Salesperson"})
	public void testGetReportByProduct() throws Exception {
		
	      String requestUrl = "/reports/product/last_7_days";
		
	      mockMvc.perform(get(requestUrl)).andExpect(status().isOk()).andDo(print());
	}
		
		
}
