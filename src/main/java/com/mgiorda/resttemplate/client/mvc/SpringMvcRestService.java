package com.mgiorda.resttemplate.client.mvc;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.mgiorda.resttemplate.client.RestService;
import com.mgiorda.resttemplate.util.MethodInvocation;

public class SpringMvcRestService<T, R> implements RestService<T, R> {

	private final MethodInvocation<T, R> methodInvacation;
	private final SpringMvcServiceData<T, R> mvcServiceData;

	public SpringMvcRestService(MethodInvocation<T, R> methodInvacation) {

		Objects.requireNonNull(methodInvacation, "Method invocation cannot be null");

		this.methodInvacation = methodInvacation;
		this.mvcServiceData = new SpringMvcServiceData<>(methodInvacation.getInterfaceClass(),
				methodInvacation.getMethod(), methodInvacation.getArgs());
	}

	@Override
	public HttpMethod getHttpMethod() {

		return mvcServiceData.getMethodHttpMethods()
				.map(httpMethods -> httpMethods.get(0))
				.orElseGet(() -> mvcServiceData.getClassHttpMethods()
						.map(classHttpMethods -> classHttpMethods.get(0))
						.orElse(HttpMethod.GET));
	}

	@Override
	public String getServiceUrl() {

		String basePath = mvcServiceData.getClassUrls().map(urls -> urls[0]).orElse("");

		String servicePath = mvcServiceData.getMethodUrls().map(urls -> urls[0]).orElse("");

		final StringBuilder serviceUrl = new StringBuilder(basePath + servicePath);

		mvcServiceData.getParmetersForAnnotation(RequestParam.class).ifPresent(requestParams -> {

			serviceUrl.append(serviceUrl.toString().contains("\\?") ? "&" : "?");
			requestParams.forEach(requestParam -> {

				RequestParam ann = requestParam.getAnnotation(RequestParam.class).get();
				serviceUrl.append(String.format("%s={%s}", ann.value(), ann.value()));
			});
		});

		return serviceUrl.toString();
	}

	@Override
	public Class<R> getResponseType() {

		@SuppressWarnings("unchecked")
		Class<R> returnType = (Class<R>) methodInvacation.getMethod()
			.getReturnType();

		return returnType;
	}

	@Override
	public Optional<Object> getRequestBody() {

		Optional<Optional<Object>> result = mvcServiceData.getParmetersForAnnotation(RequestBody.class)
				.map(parameters -> parameters.get(0))
				.map(parameter -> Optional.ofNullable(parameter.getValue()));

		return result.isPresent() ? result.get() : Optional.empty();
	}

	@Override
	// TODO: Compose in coordination to serviceUrl
	public Optional<Object[]> getUriVariables() {

		return mvcServiceData.getParameters()
				.map(params -> params.stream()
						.filter(param -> !param.getAnnotation(RequestBody.class).isPresent())
						.map(param -> param.getValue()).toArray());
	}

	@Override
	public Optional<HttpHeaders> getHeaders() {

		final HttpHeaders httpHeaders = new HttpHeaders();

		mvcServiceData.getClassHeaders().ifPresent(headers -> addHeaders(headers, httpHeaders));

		mvcServiceData.getMethodHeaders().ifPresent(headers -> addHeaders(headers, httpHeaders));

		getContentTypeHeader().ifPresent(contentType -> httpHeaders.add("Content-Type", contentType));

		return Optional.ofNullable(httpHeaders.isEmpty() ? null : httpHeaders);
	}

	private void addHeaders(String[] headers, HttpHeaders httpHeaders) {

		Arrays.stream(headers).forEach(header -> {

			String[] headerSplit = header.split("=");
			String headerName = headerSplit[0];
			String headerValue = (headerSplit.length > 0) ? headerSplit[1] : "";

			httpHeaders.add(headerName, headerValue);
		});
	}

	private Optional<String> getContentTypeHeader() {

		Optional<String> consumesValue = mvcServiceData.getMethodConsumes().filter(consumes -> consumes.length == 1)
				.map(consumes -> consumes[0]);

		if (!consumesValue.isPresent()) {
			consumesValue = mvcServiceData.getClassConsumes().filter(consumes -> consumes.length == 1)
					.map(consumes -> consumes[0]);
		}

		return consumesValue;
	}

	public static <T, R> RestService<T, R> New(MethodInvocation<T, R> methodInvocation) {
		return new SpringMvcRestService<>(methodInvocation);
	}
}