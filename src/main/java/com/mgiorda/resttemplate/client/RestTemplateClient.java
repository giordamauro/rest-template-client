package com.mgiorda.resttemplate.client;

import org.springframework.web.client.RestTemplate;

import com.mgiorda.resttemplate.util.MethodInvocation;

public interface RestTemplateClient {

	RestTemplate getRestTemplate();

	String getHostUrl();

	default <T, E> RestTemplateRequest<E> Request(MethodInvocation<T, E> methodInfo) {
		
		RestService<T, E> restService = RestService.New(methodInfo);
		RestTemplateRequest<E> client = new RestTemplateRequest<>(restService, getRestTemplate(), getHostUrl());
		
		return client;
	}
	
	static RestTemplateClientBuilder host(String hostUrl){
		return new RestTemplateClientBuilder(hostUrl);
	}
}