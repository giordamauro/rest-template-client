package com.mgiorda.resttemplate.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/users")
public interface UsersController{
	
	@RequestMapping
	void simpleEmptyService();
	
	@RequestMapping("/hello/{hello}")
	String helloService(@PathVariable("hello") String helloValue);
}
