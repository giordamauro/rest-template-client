package com.mgiorda.resttemplate.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControlerTestImpl implements UserController{

	static final String TEST_HELLO_SERVICE_FORMAT = "HelloValue: %s, QueryValue %s";
	
	@Override
	public void simpleEmptyService() {
		
		System.out.println("I am doing something here");
	}

	@Override
	public String helloService(@PathVariable("hello") String helloValue, @RequestParam("queryParam") String queryValue) {
		
		return String.format(TEST_HELLO_SERVICE_FORMAT, helloValue, queryValue);
	}

}
