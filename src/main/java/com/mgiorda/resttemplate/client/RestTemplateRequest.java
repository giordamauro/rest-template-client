package com.mgiorda.resttemplate.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestTemplateRequest {

	private final RestTemplate restTemplate;
	private final String hostUrl;
	private final RestService<?> restService;
	
	private Object[] uriVariables;
	private final Map<String, List<String>> headers = new HashMap<>();
	private Object body;
	
	RestTemplateRequest(RestService<?> restService, RestTemplate restTemplate, String hostUrl) {
		
		Objects.requireNonNull(restService);
		Objects.requireNonNull(restTemplate);
		Objects.requireNonNull(hostUrl);
		
		this.restTemplate = restTemplate;
		this.hostUrl = hostUrl;
		this.restService = restService;
		this.uriVariables = restService.getDefaultUriVariables();
	}
	
	public RestTemplateRequest withUriVariables(Object... uriVariables){
		
		Objects.nonNull(uriVariables);
		
		this.uriVariables = uriVariables;
		
		return this;
	}
	
	public RestTemplateRequest withHeader(String header, String value){
			
		List<String> values = Optional.ofNullable(headers.get(header))
				.orElseGet(() -> {
					
					List<String> newList = new ArrayList<>();
					headers.put(header, newList);
					
					return newList;
				});
		
		values.add(value);
		
		return this;
	}
	
	public RestTemplateRequest withBody(Object body){
		
		this.body = body;
		
		return this;
	}
	
	public void send(){
		sendGet(null);
	}
	
	public RestTemplateRequest withContentType(String contentType){
		return withHeader("Content-Type", contentType);
	}
	
	public <T> T sendGet(Class<T> responseClass){

		String serviceUrl = restService.getUrl();
		HttpMethod method = restService.getHttpMethod();
		
		HttpEntity<Object> requestEntity = getHttpHeaders().map(httpHeaders -> new HttpEntity<>(body, httpHeaders))
				.orElse(new HttpEntity<>(body));
		
		ResponseEntity<T> exchangeResponse = restTemplate.exchange(hostUrl + serviceUrl, method, requestEntity, responseClass, uriVariables);
        
		return Optional.ofNullable(responseClass)
				.map(responseType -> exchangeResponse.getBody())
				.orElse(null);
	}
	
	private Optional<HttpHeaders> getHttpHeaders() {

		Optional<HttpHeaders> result = Optional.empty();
		
		if(!headers.isEmpty()){
			HttpHeaders httpHeaders = new HttpHeaders();
		
			headers.entrySet()
				.parallelStream()
				.forEach(header -> {
				
					header.getValue()
						.forEach(value-> httpHeaders.add(header.getKey(), value));
				});
			result = Optional.of(httpHeaders);
		}
		return result;
	}
}
