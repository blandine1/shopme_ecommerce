package com.shopme.paypal;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.shopme.checkout.paypal.PaypalOrderResponse;

public class PaypalApitest {
	private static final String BASE_URL = "https://api.sandbox.paypal.com";
	private static final String GET_ORDER_API= "/v2/checkout/orders/";
	private static final String CLIENT_ID = "AU8tFwWeZBQikkuOz_GRuRSWLYMssrp8nvwb-wFquTKs4-faZ7rlHyjsUpEZdgz4cyRE5JgjhvrQSfZ6";
	private static final String CLIENT_SECRET = "EKXnKLT3qdblrkFy-lsdc-ACGuGOoUbjJNv_DhiKa3GyCg9lQ5TNxASKRm77LLqJ0z4AwAC3w8LTxGC2";
	
	
	@Test
	public void testGetORderDtails() {
		String orderId = "8EE06616XG6361843";
		String requestUrl = BASE_URL + GET_ORDER_API + orderId;
		HttpHeaders headers= new HttpHeaders();
		
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Languge", "en_US");
		headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
		
		HttpEntity<MultiValueMap<String , String>> request = new HttpEntity<>(headers);
		RestTemplate restTemplate= new RestTemplate();
		
		ResponseEntity<PaypalOrderResponse> exchange = restTemplate.exchange(requestUrl, HttpMethod.GET, request, PaypalOrderResponse.class);
		
		PaypalOrderResponse orderReponse = exchange.getBody();
		System.out.println("order id : "+ orderReponse.getId());
		System.out.println("validate id : " + orderReponse.validte(orderId));
		
	}
	

}
