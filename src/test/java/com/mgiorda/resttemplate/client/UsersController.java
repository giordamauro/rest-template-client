package com.mgiorda.resttemplate.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/users")
public interface UsersController{
	
	@RequestMapping
	void simpleEmptyService();
	
	@RequestMapping(value="/hello/{hello}")
	String helloService(@PathVariable("hello") String helloValue, @RequestParam("queryParam") String queryValue);
}
