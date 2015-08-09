package com.mgiorda.resttemplate.client;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public interface RestTemplateClient {

	RestTemplate getRestTemplate();

	String getHostUrl();

	default <T, E> RestTemplateRequest<E> Request(RestService<T, E> restService) {
		return  new RestTemplateRequest<>(restService, getRestTemplate(), getHostUrl());
	}
	
	default RestTemplateRequest<?> Request(HttpMethod method, String serviceUrl) {
		return new RestTemplateRequest<>(method, serviceUrl, getRestTemplate(), getHostUrl());
	}
	
	static RestTemplateClientBuilder host(String hostUrl){
		return new RestTemplateClientBuilder(hostUrl);
	}
}