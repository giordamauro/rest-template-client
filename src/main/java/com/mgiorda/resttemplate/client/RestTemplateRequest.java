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

	RestTemplateRequest(HttpMethod httpMethod, String serviceUrl, RestTemplateClient client) {

		Objects.requireNonNull(httpMethod, "Http method cannot be null");
		Objects.requireNonNull(serviceUrl, "Service url cannot be null");
		Objects.requireNonNull(client, "RestTemplate client cannot be null");

		this.restTemplate = client.getRestTemplate();
		this.hostUrl = client.getHostUrl();

		this.httpMethod = httpMethod;
		this.serviceUrl = serviceUrl;

		client.getDefaultHeaders()
			.map(headers -> headers.entrySet())
			.ifPresent(headers -> headers.forEach(header -> withHeader(header.getKey(), header.getValue())));
	}

	RestTemplateRequest(RestService<?, R> restService, RestTemplateClient client) {

		this(restService.getHttpMethod(), restService.getServiceUrl(), client);
		this.uriVariables = restService.getUriVariables();

		Class<R> responseClass = restService.getResponseType();
		this.responseClass = (!responseClass.equals(Void.TYPE)) ? responseClass : null;
	}

	private RestTemplateRequest(RestTemplateRequest<?> request, Class<R> responseClass) {

		this.restTemplate = request.restTemplate;
		this.hostUrl = request.hostUrl;
		this.httpMethod = request.httpMethod;
		this.serviceUrl = request.serviceUrl;
		this.uriVariables = request.uriVariables;
		this.responseClass = responseClass;
	}

	public RestTemplateRequest<R> withUriVariables(Object... uriVariables) {

		this.uriVariables = Optional.of(uriVariables);

		return this;
	}

	public RestTemplateRequest<R> withHeader(String header, String value) {

		Objects.requireNonNull(header, "Header cannot be null");
		Objects.requireNonNull(value, "Value cannot be null");

		HttpHeaders headers = httpHeaders.orElseGet(() -> {
			this.httpHeaders = Optional.of(new HttpHeaders());
			return httpHeaders.get();
		});
		headers.add(header, value);

		return this;
	}

	public RestTemplateRequest<R> withBody(Object body) {

		Objects.requireNonNull(body, "Body cannot be null");

		this.body = body;

		return this;
	}

	public RestTemplateRequest<R> withQueryParam(String queryParam, String value) {

		Objects.requireNonNull(queryParam, "Query param name cannot be null");
		Objects.requireNonNull(value, "Value cannot be null");

		String separator = serviceUrl.contains("\\?") ? "&" : "?";
		this.serviceUrl += String.format("%s%s=%s", separator, queryParam, value);

		return this;
	}

	public RestTemplateRequest<R> withHttpMethod(HttpMethod httpMethod) {

		Objects.requireNonNull(httpMethod, "Http method cannot be null");

		this.httpMethod = httpMethod;

		return this;
	}

	public RestTemplateRequest<R> withServiceUrl(String serviceUrl) {

		Objects.requireNonNull(serviceUrl, "Service url cannot be null");

		this.serviceUrl = serviceUrl;

		return this;
	}

	public RestTemplateRequest<R> withContentType(MediaType contentType) {
		return withHeader("Content-Type", contentType.getType());
	}

	public RestTemplateRequest<R> withHttpHeaders(HttpHeaders httpHeaders) {

		Objects.requireNonNull(httpHeaders, "Http headers cannot be null");
		this.httpHeaders = Optional.of(httpHeaders);

		return this;
	}

	public RestTemplateRequest<R> withRequestHttpEntity(HttpEntity<?> requestEntity) {

		Objects.requireNonNull(requestEntity, "Request entity cannot be null");

		@SuppressWarnings("unchecked")
		HttpEntity<Object> httpEntity = (HttpEntity<Object>) requestEntity;
		this.httpEntity = Optional.of(httpEntity);

		return this;
	}

	public <E> RestTemplateRequest<E> withResponseAs(Class<E> responseClass) {

		Objects.requireNonNull(responseClass, "Response Class cannot be null");

		return new RestTemplateRequest<>(this, responseClass);
	}

	public ResponseEntity<R> getResponseEntity() {

		HttpEntity<Object> requestEntity = httpEntity.orElseGet(() -> httpHeaders
				.map(httpHeaders -> new HttpEntity<>(body, httpHeaders))
				.orElse(new HttpEntity<>(body)));

		Object[] uriVars = uriVariables.orElse(new Object[] {});

		ResponseEntity<R> exchangeResponse = restTemplate.exchange(hostUrl + serviceUrl, httpMethod, requestEntity,
				responseClass, uriVars);

		return exchangeResponse;
	}

	public R sendAndGet() {

		ResponseEntity<R> response = getResponseEntity();

		return Optional.ofNullable(responseClass).map(responseType -> response.getBody()).orElse(null);
	}
}
