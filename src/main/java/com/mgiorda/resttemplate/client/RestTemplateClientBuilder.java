package com.mgiorda.resttemplate.client;

import java.util.Objects;

import org.springframework.web.client.RestTemplate;

public class RestTemplateClientBuilder implements RestTemplateClient {

	private final String hostUrl;
	private RestTemplate restTemplate;
	
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

	@Override
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	@Override
	public String getHostUrl() {
		return hostUrl;
	}
}
