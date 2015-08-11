package com.mgiorda.resttemplate.client;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public interface RestService<T, R> {

	HttpMethod getHttpMethod();

	String getServiceUrl();

	Class<R> getResponseType();

	Optional<Object[]> getUriVariables();

	Optional<HttpHeaders> getHeaders();

	Optional<Object> getRequestBody();

	default <E> E map(Function<RestService<T, R>, E> mapper) {
		return mapper.apply(this);
	}
}