package com.mgiorda.resttemplate.client;

import com.mgiorda.resttemplate.util.Interfaces;

public class TestClass {
	
	public static void main(String[] args) {
		
		RestTemplateClient restClient = RestTemplateClient.host("http://localhost:8080");

		String returnValue1 = Interfaces.methodCall(User.class, User::empty)
				.map(restClient::Request)
				.withUriVariables(new Object[]{})
				.sendGet(String.class);
		
		String returnValue2 = Interfaces.methodCall(User.class, user -> user.hola("hola"))
			.map(restClient::Request)
			.sendGet(String.class);
	
		System.out.println(returnValue1 + returnValue2);
	}
}
