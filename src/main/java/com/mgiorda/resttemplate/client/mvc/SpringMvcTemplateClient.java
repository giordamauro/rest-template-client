package com.mgiorda.resttemplate.client.mvc;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.mgiorda.resttemplate.client.RestTemplateClient;
import com.mgiorda.resttemplate.client.RestTemplateRequest;
import com.mgiorda.resttemplate.util.Interfaces;

public class SpringMvcTemplateClient implements RestTemplateClient{

	private final RestTemplateClient restClient;
	
	public SpringMvcTemplateClient(RestTemplateClient restTemplateClient) {
	
		Objects.requireNonNull(restTemplateClient, "Rest template client cannot be null");
		this.restClient = restTemplateClient;
	}

	public <T, R> RestTemplateRequest<R> newRequest(Class<T> interfaceClass, Function<T, R> function){
		
		return Interfaces.method(interfaceClass, function)
				.map(SpringMvcRestService::New)
				.map(this::Request);
	}
	
	public <T> RestTemplateRequest<?> newVoidRequest(Class<T> interfaceClass, Consumer<T> consumer){
		
		return Interfaces.voidMethod(interfaceClass, consumer)
				.map(SpringMvcRestService::New)
				.map(this::Request);
	}
	
	public <T> void send(Class<T> interfaceClass, Consumer<T> consumer){
		
		getVoidResponseEntity(interfaceClass, consumer);
	}

	public <T, R> ResponseEntity<?> getVoidResponseEntity(Class<T> interfaceClass, Consumer<T> consumer){

		return newVoidRequest(interfaceClass, consumer).getResponseEntity();
	}
	
	public <T, R> R sendAndGet(Class<T> interfaceClass, Function<T, R> function){

		return newRequest(interfaceClass, function).sendAndGet();
	}
	
	public <T, R> ResponseEntity<R> getResponseEntity(Class<T> interfaceClass, Function<T, R> function){

		return newRequest(interfaceClass, function).getResponseEntity();
	}

	@Override
	public RestTemplate getRestTemplate() {
		return restClient.getRestTemplate();
	}

	@Override
	public String getHostUrl() {
		return restClient.getHostUrl();
	}

	@Override
	public Optional<Map<String, String>> getDefaultHeaders() {
		return restClient.getDefaultHeaders();
	}
}