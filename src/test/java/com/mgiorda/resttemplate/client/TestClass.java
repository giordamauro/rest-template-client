package com.mgiorda.resttemplate.client;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.mgiorda.resttemplate.util.Interfaces;

public class TestClass {
		
	/**
	*  TODO: Allow overriding any of the Http RestTemplate parameters - Add getResponseEntityAs() with different type.
	*/
	
	private RestTemplateClient restClient = RestTemplateClient.host("http://localhost:8080")
	/*					.andRestTemplate(new RestTemplate())*/;
		
	@Test
	public void testRequestSimpleService(){
		
		Interfaces.voidMethod(UsersController.class, UsersController::simpleEmptyService)
			.map(restClient::Request)
			.send();
	}

	@Test
	public void testRequestServiceUsingLambda(){
		
		String returnValue = Interfaces.method(UsersController.class, controller -> controller.helloService("hola"))
				.map(restClient::Request)
				.send();

		System.out.println(returnValue);
	}
	
	@Test
	public void testOverridingHttpValues(){
		
		ResponseEntity<String> responseEntity = Interfaces.method(UsersController.class, controller -> controller.helloService("hola"))
				.map(restClient::Request)
				.withUriVariables(new Object[] {"differentHola"})
				.withContentType(MediaType.APPLICATION_JSON)
				.withHeader("Accept", "application/json")
				.withBody("Sample body content")
				.getResponseEntity();
		
		System.out.println(responseEntity.getStatusCode());
	}

	
}
