package com.mgiorda.resttemplate.client;

import com.mgiorda.resttemplate.util.Interfaces;

public class TestClass {
	public static void main(String[] args) {
		
		RestTemplateClient restClient = RestTemplateClient.host("http://localhost:8080")
		/*			.andRestTemplate(new RestTemplate())*/;

	// Sending request to 'simpleEmptyService':
		Interfaces.methodCall(UsersController.class, UsersController::simpleEmptyService)
				.map(restClient::Request)
		//			.withUriVariables(new Object[]{})
		//			.withHeader("Accept", "application/json")
		//			.withBody("Sample body content")
				.send();
		
	// Requesting a 2nd service using a Lambda function:
		String returnValue = Interfaces.methodCall(UsersController.class, controller -> controller.helloService("hola"))
			.map(restClient::Request)
			.sendGet(String.class);
	
		System.out.println(returnValue);
	}
}
