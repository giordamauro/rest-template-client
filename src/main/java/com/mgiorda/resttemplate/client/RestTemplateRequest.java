package com.mgiorda.resttemplate.client;

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
	
	private HttpMethod httpMethod;
	private String serviceUrl;
	private Object body;
	private Class<R> responseClass;
	
	private Optional<Object[]> uriVariables;
	private Optional<HttpHeaders> httpHeaders = Optional.empty();
	private Optional<HttpEntity<Object>> httpEntity = Optional.empty();
	
	RestTemplateRequest(HttpMethod httpMethod, String serviceUrl, RestTemplate restTemplate, String hostUrl) {
		
		Objects.requireNonNull(httpMethod);
		Objects.requireNonNull(serviceUrl);
		Objects.requireNonNull(restTemplate);
		Objects.requireNonNull(hostUrl);
		
		this.restTemplate = restTemplate;
		this.hostUrl = hostUrl;
		
		this.httpMethod = httpMethod;
		this.serviceUrl = serviceUrl;
	}
	
	RestTemplateRequest(RestService<?, R> restService, RestTemplate restTemplate, String hostUrl) {
		
		this(restService.getHttpMethod(), restService.getServiceUrl(), restTemplate, hostUrl);
		this.uriVariables = restService.getUriVariables();
		
		Class<R> responseClass = restService.getResponseType();
		this.responseClass = (!responseClass.equals(Void.TYPE)) ? responseClass : null;
	}
	
	private RestTemplateRequest(RestTemplateRequest<?> request, Class<R> responseClass){
		
		this.restTemplate = request.restTemplate;
		this.hostUrl = request.hostUrl;
		this.httpMethod = request.httpMethod;
		this.serviceUrl = request.serviceUrl;
		this.uriVariables = request.uriVariables;
		this.responseClass = responseClass;
	}
		
	public RestTemplateRequest<R> withUriVariables(Object... uriVariables){
		
		this.uriVariables = Optional.of(uriVariables);
		
		return this;
	}
	
	public RestTemplateRequest<R> withHeader(String header, String value){

		Objects.nonNull(header);
		Objects.nonNull(value);
		
		HttpHeaders headers = httpHeaders.orElseGet(() -> {
			this.httpHeaders = Optional.of(new HttpHeaders());
			return httpHeaders.get();
		});
		headers.add(header, value);
		
		return this;
	}
	
	public RestTemplateRequest<R> withBody(Object body){
		
		Objects.nonNull(body);
		
		this.body = body;
		
		return this;
	}
	
	public RestTemplateRequest<R> withHttpMethod(HttpMethod httpMethod){
	
		Objects.nonNull(body);
		
		this.httpMethod = httpMethod;
		
		return this;
	}
	
	public RestTemplateRequest<R> withServiceUrl(String serviceUrl){

		Objects.nonNull(serviceUrl);
		
		this.serviceUrl = serviceUrl;
		
		return this;
	}
	
	public RestTemplateRequest<R> withContentType(MediaType contentType){
		return withHeader("Content-Type", contentType.getType());
	}
	
	public RestTemplateRequest<R> withHttpHeaders(HttpHeaders httpHeaders){

		this.httpHeaders = Optional.of(httpHeaders);
		
		return this;
	}

	public RestTemplateRequest<R> withRequestHttpEntity(HttpEntity<?> requestEntity){

		Objects.requireNonNull(requestEntity);
		
		@SuppressWarnings("unchecked")
		HttpEntity<Object> httpEntity = (HttpEntity<Object>) requestEntity;
		this.httpEntity = Optional.of(httpEntity);
		
		return this;
	}
	
	public <E> RestTemplateRequest<E> withResponseAs(Class<E> responseClass){

		Objects.requireNonNull(responseClass);
		
		return new RestTemplateRequest<>(this, responseClass);
	}
	
	public ResponseEntity<R> getResponseEntity(){
		
		HttpEntity<Object> requestEntity = httpEntity
				.orElseGet(() -> 
					httpHeaders
						.map(httpHeaders -> new HttpEntity<>(body, httpHeaders))
						.orElse(new HttpEntity<>(body))
						);
		
		Object[] uriVars = uriVariables.orElse(new Object[]{});
		
		ResponseEntity<R> exchangeResponse = restTemplate.exchange(hostUrl + serviceUrl, httpMethod, requestEntity, responseClass, uriVars);

		return exchangeResponse;
	}
	
	public R send(){

		ResponseEntity<R> response = getResponseEntity();
		
		return Optional.ofNullable(responseClass)
				.map(responseType -> response.getBody())
				.orElse(null);
	}
}
