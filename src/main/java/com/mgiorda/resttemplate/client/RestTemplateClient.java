package com.mgiorda.resttemplate.client;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public interface RestTemplateClient {

	RestTemplate getRestTemplate();

	String getHostUrl();

	Optional<Map<String, String>> getDefaultHeaders();

	default <T, E> RestTemplateRequest<E> Request(RestService<T, E> restService) {
		
		Objects.requireNonNull(restService, "Rest service cannot be null");
		
		return new RestTemplateRequest<>(restService, this);
	}

	default RestTemplateRequest<?> newRequest(HttpMethod method, String serviceUrl) {
		
		Objects.requireNonNull(method, "Method cannot be null");
		Objects.requireNonNull(serviceUrl, "service url cannot be null");
		
		return new RestTemplateRequest<>(method, serviceUrl, this);
	}
	
	default <R> R map(Function<RestTemplateClient, R> function){
		
		Objects.requireNonNull(function, "Function cannot be null");
		
		return function.apply(this);
	}

	static RestTemplateClientBuilder host(String hostUrl) {
		
		Objects.requireNonNull(hostUrl, "Host url cannot be null");
		
		return new RestTemplateClientBuilder(hostUrl);
	}
}