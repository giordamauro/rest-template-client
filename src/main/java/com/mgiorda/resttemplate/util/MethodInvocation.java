package com.mgiorda.resttemplate.util;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

public class MethodInvocation<T, E>{
	
	private final Class<T> interfaceClass;
	private final Method method;
	private final Object[] args;
	
	public MethodInvocation(Class<T> proxyClass, Method method, Object[] args) {
		
		this.interfaceClass = proxyClass;
		this.method = method;
		this.args = args;
	}

	public Class<T> getInterfaceClass() {
		return interfaceClass;
	}

	public Method getMethod() {
		return method;
	}
	
	public Optional<Object[]> getArgs(){
		return Optional.ofNullable(args);
	}
	
	@SuppressWarnings("unchecked")
	public Class<E> getReturnType(){
		return (Class<E>) method.getReturnType();
	}
	
	public <R> R map(Function<MethodInvocation<T, E>, R> mapper){
		return mapper.apply(this);
	}
}