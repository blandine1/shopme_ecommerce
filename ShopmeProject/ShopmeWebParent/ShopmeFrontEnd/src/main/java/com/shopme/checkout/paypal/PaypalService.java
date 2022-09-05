package com.shopme.checkout.paypal;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.shopme.setting.PaymentSettingBag;
import com.shopme.setting.SettingService;

@Component
public class PaypalService {
	private static final String GET_ORDER_API= "/v2/checkout/orders/";
	
	@Autowired private SettingService settingService;
	
	public boolean validateOrder(String orderId) throws PaypalApiException {
        PaypalOrderResponse orderReponse = getOrderDetails(orderId);
		
		return orderReponse.validte(orderId);
	}

	private PaypalOrderResponse getOrderDetails(String orderId) throws PaypalApiException {
		ResponseEntity<PaypalOrderResponse> response = makeRequest(orderId);
		
		HttpStatus statusCode = response.getStatusCode();
		
		if(!statusCode.equals(HttpStatus.OK)) {
			throwEceptionForNoneOkResponse(statusCode);
		 }
		
		
		return response.getBody();
	}

	private ResponseEntity<PaypalOrderResponse> makeRequest(String orderId) {
		PaymentSettingBag paymentSetting = settingService.getPaymentSetting();
        String baseUrl = paymentSetting.getURL();
        String requestUrl = baseUrl+ GET_ORDER_API+ orderId;
        String clientId= paymentSetting.getClientId();
        String clientSecret = paymentSetting.getClientSecret();
		
		HttpHeaders headers= new HttpHeaders();
		
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Languge", "en_US");
		headers.setBasicAuth(clientId, clientSecret);
		
		HttpEntity<MultiValueMap<String , String>> request = new HttpEntity<>(headers);
		RestTemplate restTemplate= new RestTemplate();
		
		ResponseEntity<PaypalOrderResponse> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request, PaypalOrderResponse.class);
		return response;
	}

	private void throwEceptionForNoneOkResponse(HttpStatus statusCode) throws PaypalApiException {
		String message = "";
		switch (statusCode) {
		case NOT_FOUND: 
		    message ="Order Id not found";
		case BAD_REQUEST:
			 message = "YOU made bad request to PAYPAL";
		case INTERNAL_SERVER_ERROR:
			message= "Internal server error";
		default:
		 message= "paypal returned non-ok status code";
		}
		
		throw new PaypalApiException(message);
	}
}
