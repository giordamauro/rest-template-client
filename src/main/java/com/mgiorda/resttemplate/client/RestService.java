package com.mgiorda.resttemplate.client;

import java.util.Objects;
import java.util.Optional;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mgiorda.resttemplate.util.MethodInfo;

public class RestService<T, E>{
	
//	TODO: Add complete information (consumes, produces, etc)
//	TODO: Support multiple values (services Urls, methods, etc)
	
	private final RequestMethod method;
	private final String serviceUrl;
	private final Class<E> returnType;
	private final Object[] defaultUriVariables;
	
	public RestService(MethodInfo<T, E> info){
		
		Objects.requireNonNull(info);
		
		RequestMapping methodMappingAnn = AnnotationUtils.findAnnotation(info.getMethod(), RequestMapping.class);
		Objects.requireNonNull(methodMappingAnn, String.format("Service method '%s' is not annotated with @RequestMapping", info.getMethod()));		
		
		RequestMapping classMappingAnn = AnnotationUtils.findAnnotation(info.getInterfaceClass(), RequestMapping.class);
		String basePathUrl = Optional.ofNullable(classMappingAnn)
				.filter(ann -> ann.value().length != 0)
				.map(ann -> ann.value()[0])
				.orElse("");

		String servicePathUrl = Optional.of(methodMappingAnn.value())
				.filter(values -> values.length != 0)
				.map(values -> values[0])
				.orElse("");
		
		this.serviceUrl = basePathUrl + servicePathUrl;
		
		this.method = Optional.of(methodMappingAnn.method())
				.filter(values -> values.length != 0)
				.map(values -> values[0])
				.orElse(RequestMethod.GET);
		
		@SuppressWarnings("unchecked")
		Class<E> returnType = (Class<E>) info.getMethod().getReturnType();
		this.returnType = returnType;
		
		this.defaultUriVariables = info.getArgs().orElse(new Object[]{});
	}
	
	public RequestMethod getRequestMethod() {
		return method;
	}
	
	public HttpMethod getHttpMethod() {
		return HttpMethod.valueOf(method.name());
	}

	public String getUrl() {
		return serviceUrl;
	}

	public Class<E> getResponseType() {
		return returnType;
	}
	
	public Object[] getDefaultUriVariables(){
		return defaultUriVariables;
	}

	public static <T, E> RestService<T, E> New(MethodInfo<T, E> info){
		return new RestService<T, E>(info);
	}
}