package com.mgiorda.resttemplate.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.web.client.RestTemplate;

public class RestTemplateClientBuilder implements RestTemplateClient {

	private RestTemplate restTemplate;
	
	private final String hostUrl;
	private final Map<String, String> defaultHeaders = new HashMap<>();
	
	public RestTemplateClientBuilder(String hostUrl) {
		
		Objects.requireNonNull(hostUrl);
	
		this.hostUrl = hostUrl;
		this.restTemplate = new RestTemplate();
	}
	
	public RestTemplateClientBuilder andRestTemplate(RestTemplate restTemplate){
		
		Objects.requireNonNull(restTemplate);
		this.restTemplate = restTemplate;
		
		return this;
	}
	
	public RestTemplateClientBuilder setDefaultHeader(String headerName, String value){
		
		Objects.requireNonNull(headerName);
		Objects.requireNonNull(value);

		defaultHeaders.put(headerName, value);
		
		return this;
	}
	
	@Override
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	@Override
	public String getHostUrl() {
		return hostUrl;
	}

	@Override
	public Optional<Map<String, String>> getDefaultHeaders() {
		return Optional.ofNullable(defaultHeaders.isEmpty() ? null : defaultHeaders);
	}
}
