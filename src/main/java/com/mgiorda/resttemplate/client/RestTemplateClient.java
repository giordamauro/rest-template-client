package com.mgiorda.resttemplate.client;

import org.springframework.web.client.RestTemplate;

import com.mgiorda.resttemplate.util.MethodInfo;

public interface RestTemplateClient {

	RestTemplate getRestTemplate();

	String getHostUrl();

	default <T> RestTemplateRequest Request(MethodInfo<T> methodInfo) {
		
		RestService<T> restService = RestService.New(methodInfo);
		RestTemplateRequest client = new RestTemplateRequest(restService, getRestTemplate(), getHostUrl());
		
		return client;
	}
	
	static RestTemplateClientBuilder host(String hostUrl){
		return new RestTemplateClientBuilder(hostUrl);
	}
}