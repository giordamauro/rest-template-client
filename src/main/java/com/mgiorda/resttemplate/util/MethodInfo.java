package com.mgiorda.resttemplate.util;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

public class MethodInfo<T>{
	
	private final Class<T> interfaceClass;
	private final Method method;
	private final Object[] args;
	
	public MethodInfo(Class<T> proxyClass, Method method, Object[] args) {
		
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
	
	public <R> R map(Function<MethodInfo<T>, R> mapper){
		
		return mapper.apply(this);
	}

	@Override
	public String toString() {
		return "MethodInfo [interfaceClass=" + interfaceClass + ", method=" + method + "]";
	}
}