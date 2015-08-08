package com.mgiorda.resttemplate.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/basePath")
public interface User{
	
	@RequestMapping("/innerPath/{hello}")
	int hola(@PathVariable("hello") String helloParam);

	@RequestMapping
	void empty();
}
