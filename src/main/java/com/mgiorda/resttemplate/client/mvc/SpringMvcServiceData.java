package com.mgiorda.resttemplate.client.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

class SpringMvcServiceData<T, R>{
	
	private final Optional<RequestMapping> classMappingAnn;
	private final RequestMapping methodMappingAnn;
	private final Optional<List<AnnotatedParameter<Object>>> parameters;
	
	public SpringMvcServiceData(Class<T> serviceClass, Method serviceMethod, Optional<Object[]> methodParameters){
		
		this.methodMappingAnn = AnnotationUtils.findAnnotation(serviceMethod, RequestMapping.class);
		Objects.requireNonNull(methodMappingAnn, String.format("Service method '%s' is not annotated with @RequestMapping", serviceMethod));		
		
		RequestMapping classMappingAnn = AnnotationUtils.findAnnotation(serviceClass, RequestMapping.class);
		this.classMappingAnn = Optional.ofNullable(classMappingAnn);
		
		
		Annotation[][] parameterAnnotations = serviceMethod.getParameterAnnotations();
		this.parameters = methodParameters.
				map(values -> {
					List<AnnotatedParameter<Object>> parametersList = new ArrayList<>();
					for(int i = 0; i < values.length; i++){
							List<Annotation> annotations = Arrays.asList(parameterAnnotations[i]);
							parametersList.add(new AnnotatedParameter<>(values[i], annotations));
					}
					return parametersList;
				});			
	}
	
	public Optional<String[]> getClassUrls(){
		return classMappingAnn
				.map(ann -> ann.value());
	}
	
	public Optional<String[]> getMethodUrls(){
		
		String[] urls = methodMappingAnn.value();
		
		return Optional.ofNullable(urls.length > 0 ? urls : null); 
	}
	
	public Optional<List<HttpMethod>> getClassHttpMethods(){
		
		return classMappingAnn
				.filter(ann -> ann.method().length > 0)
				.map(ann -> {
					RequestMethod[] requestMethods = ann.method();
					return Arrays.stream(requestMethods)	
						.map(requestMethod -> HttpMethod.valueOf(requestMethod.name()))
						.collect(Collectors.toList());
				});
	}
	
	public Optional<List<HttpMethod>> getMethodHttpMethods() {

		List<HttpMethod> httpMethods = Arrays.stream(methodMappingAnn.method())
				.map(requestMethod -> HttpMethod.valueOf(requestMethod.name()))
				.collect(Collectors.toList());
		
		return Optional.ofNullable(httpMethods.isEmpty() ? null : httpMethods);
	}
	
	public Optional<String[]> getClassParams(){
		
		return classMappingAnn
				.map(ann -> ann.params());
	}
	
	public Optional<String[]> getMethodParams(){
		String[] params = methodMappingAnn.params();

		 return Optional.ofNullable(params.length > 0 ? params : null);
	}
	
	public Optional<String[]> getClassHeaders(){
		
		return classMappingAnn
				.filter(ann -> ann.headers().length > 0)
				.map(ann -> ann.headers());	
	}
	
	public Optional<String[]> getMethodHeaders(){
		 
		String[] headers = methodMappingAnn.headers();
		 
		 return Optional.ofNullable(headers.length > 0 ? headers : null);
	}
	
	public Optional<String[]> getClassConsumes(){
		
		return classMappingAnn
				.filter(ann -> ann.consumes().length > 0)
				.map(ann -> ann.consumes());
	}
	
	public Optional<String[]> getMethodConsumes(){
		String[] consumes = methodMappingAnn.consumes();
		
		return Optional.ofNullable(consumes.length > 0 ? consumes : null);
	}

	public Optional<String[]> getClassProduces(){
		
		return classMappingAnn
				.filter(ann -> ann.produces().length > 0)
				.map(ann -> ann.produces());
	}
	
	public Optional<String[]> getMethodProduces(){
		
		String[] produces = methodMappingAnn.produces();
		
		return Optional.ofNullable(produces.length > 0 ? produces : null);
	}
	
	public Optional<List<AnnotatedParameter<Object>>> getParameters(){
		return parameters;
	}
	
	public <E extends Annotation> Optional<List<AnnotatedParameter<Object>>> getParmetersForAnnotation(Class<E> annotationClass){
	
		return parameters
				.map(params -> 
					params.stream()
						.filter(param -> param.getAnnotation(annotationClass).isPresent())
						.collect(Collectors.toList())
						);
	}
}
