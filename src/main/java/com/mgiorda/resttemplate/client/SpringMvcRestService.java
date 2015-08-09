package com.mgiorda.resttemplate.client;

import java.util.Objects;
import java.util.Optional;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mgiorda.resttemplate.util.MethodInvocation;

public class SpringMvcRestService<T, R> implements RestService<T, R>{
	
	private final RequestMethod method;
	private final String serviceUrl;
	private final Class<R> returnType;
	
	private final Optional<Object[]> defaultUriVariables;
	private final Optional<Object> defaultRequestBody;
	private final Optional<HttpHeaders> httpHeaders;
	private final Optional<String[]> consumes;
	private final Optional<String[]> produces;
	
	public SpringMvcRestService(MethodInvocation<T, R> info){
		
		Objects.requireNonNull(info);
		
		RequestMapping methodMappingAnn = AnnotationUtils.findAnnotation(info.getMethod(), RequestMapping.class);
		Objects.requireNonNull(methodMappingAnn, String.format("Service method '%s' is not annotated with @RequestMapping", info.getMethod()));		
		
		RequestMapping classMappingAnn = AnnotationUtils.findAnnotation(info.getInterfaceClass(), RequestMapping.class);
		
		this.serviceUrl = buildServiceUrl(classMappingAnn, methodMappingAnn);
		
		this.method = Optional.of(methodMappingAnn.method())
				.filter(values -> values.length != 0)
				.map(values -> values[0])
				.orElse(RequestMethod.GET);
		
		@SuppressWarnings("unchecked")
		Class<R> returnType = (Class<R>) info.getMethod().getReturnType();
		this.returnType = returnType;
		
//		TODO:
		
		this.defaultUriVariables = info.getArgs();
		
		this.defaultRequestBody = Optional.empty();
		this.httpHeaders = Optional.empty();
		this.consumes = Optional.empty();
		this.produces = Optional.empty();
	}
	
	private String buildServiceUrl(RequestMapping classMappingAnn, RequestMapping methodMappingAnn){

		String basePathUrl = Optional.ofNullable(classMappingAnn)
				.filter(ann -> ann.value().length != 0)
				.map(ann -> ann.value()[0])
				.orElse("");

		String servicePathUrl = Optional.of(methodMappingAnn.value())
				.filter(values -> values.length != 0)
				.map(values -> values[0])
				.orElse("");
		
		return basePathUrl + servicePathUrl;
	}
	
	public RequestMethod getRequestMethod() {
		return method;
	}
	
	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.valueOf(method.name());
	}

	@Override
	public String getServiceUrl() {
		return serviceUrl;
	}

	@Override
	public Class<R> getResponseType() {
		return returnType;
	}
	
	@Override
	public Optional<Object[]> getUriVariables(){
		return defaultUriVariables;
	}

	@Override
	public Optional<HttpHeaders> getHeaders() {
		return httpHeaders;
	}

	@Override
	public Optional<Object> getRequestBody() {
		return defaultRequestBody;
	}

	@Override
	public Optional<String[]> getConsumes() {
		return consumes;
	}

	@Override
	public Optional<String[]> getProduces() {
		return produces;
	}
	
	public static<T, R> RestService<T, R> New(MethodInvocation<T, R> methodInvocation){
		return new SpringMvcRestService<>(methodInvocation);
	}
}