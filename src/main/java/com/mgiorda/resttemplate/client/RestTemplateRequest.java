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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestTemplateRequest<R> {

	private final RestTemplate restTemplate;
	private final String hostUrl;
	private final RestService<?, R> restService;
	
	private final Map<String, List<String>> headers = new HashMap<>();
	private Object[] uriVariables;
	private Object body;
	private Class<R> responseClass;
	
	RestTemplateRequest(RestService<?, R> restService, RestTemplate restTemplate, String hostUrl) {
		
		Objects.requireNonNull(restService);
		Objects.requireNonNull(restTemplate);
		Objects.requireNonNull(hostUrl);
		
		this.restTemplate = restTemplate;
		this.hostUrl = hostUrl;
		this.restService = restService;
		this.uriVariables = restService.getDefaultUriVariables();
		
		Class<R> responseClass = restService.getResponseType();
		this.responseClass = (!responseClass.equals(Void.TYPE)) ? responseClass : null;
	}
	
	public RestTemplateRequest<R> withUriVariables(Object... uriVariables){
		
		Objects.nonNull(uriVariables);
		
		this.uriVariables = uriVariables;
		
		return this;
	}
	
	public RestTemplateRequest<R> withHeader(String header, String value){

		Objects.nonNull(header);
		Objects.nonNull(value);
		
		List<String> values = Optional.ofNullable(headers.get(header))
				.orElseGet(() -> {
					
					List<String> newList = new ArrayList<>();
					headers.put(header, newList);
					
					return newList;
				});
		
		values.add(value);
		
		return this;
	}
	
	public RestTemplateRequest<R> withBody(Object body){
		
		Objects.nonNull(body);
		
		this.body = body;
		
		return this;
	}
	
	public RestTemplateRequest<R> withContentType(MediaType contentType){
		return withHeader("Content-Type", contentType.getType());
	}
	
	public ResponseEntity<R> getResponseEntity(){
		
		String serviceUrl = restService.getUrl();
		HttpMethod method = restService.getHttpMethod();
		
		HttpEntity<Object> requestEntity = buildHttpHeaders().map(httpHeaders -> new HttpEntity<>(body, httpHeaders))
				.orElse(new HttpEntity<>(body));
		
		ResponseEntity<R> exchangeResponse = restTemplate.exchange(hostUrl + serviceUrl, method, requestEntity, responseClass, uriVariables);

		return exchangeResponse;
	}
	
	public R send(){

		ResponseEntity<R> response = getResponseEntity();
		
		return Optional.ofNullable(responseClass)
				.map(responseType -> response.getBody())
				.orElse(null);
	}
	
	private Optional<HttpHeaders> buildHttpHeaders() {

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
