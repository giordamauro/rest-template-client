package com.mgiorda.resttemplate.client;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public interface RestTemplateClient {

	RestTemplate getRestTemplate();

	String getHostUrl();
	
	Optional<Map<String, String>> getDefaultHeaders();

	default <T, E> RestTemplateRequest<E> Request(RestService<T, E> restService) {
		return new RestTemplateRequest<>(restService, this);
	}
	
	default RestTemplateRequest<?> Request(HttpMethod method, String serviceUrl) {
		return new RestTemplateRequest<>(method, serviceUrl, this);
	}
	
	static RestTemplateClientBuilder host(String hostUrl){
		return new RestTemplateClientBuilder(hostUrl);
	}
}