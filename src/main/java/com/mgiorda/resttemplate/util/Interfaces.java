package com.mgiorda.resttemplate.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class Interfaces<T>{
			
	private final Class<T> proxyClass;
	
	public Interfaces(Class<T> proxyClass) {
		
		Objects.nonNull(proxyClass);
		this.proxyClass = proxyClass;
	}
	
	public MethodInvocation<T, ?> voidMethod(Consumer<T> funcion){

		ProxyFactory<T, ?> proxyFactory = new ProxyFactory<>(proxyClass);
		
		funcion.accept(proxyFactory.getProxy());
		
		return proxyFactory.getProxyInfo();
	}

	public <R> MethodInvocation<T, R> method(Function<T, R> function){

		ProxyFactory<T, R> proxyFactory = new ProxyFactory<>(proxyClass);
		
		function.apply(proxyFactory.getProxy());
		
		return proxyFactory.getProxyInfo();
	}
	
	public static <T> MethodInvocation<T, ?> voidMethod(Class<T> interfaceClass, Consumer<T> consumer){
		
		return new Interfaces<T>(interfaceClass).voidMethod(consumer);
	}
	
	public static <T, R> MethodInvocation<T, R> method(Class<T> interfaceClass, Function<T, R> function){
		return new Interfaces<T>(interfaceClass).method(function);
	}
		
	private static class ProxyFactory<T, E> implements InvocationHandler{

		private final Class<T> proxyClass;
		private final T proxy;
		
		private MethodInvocation<T, E> proxyInfo;
		
		@SuppressWarnings("unchecked")
		public ProxyFactory(Class<T> proxyClass) {
			this.proxyClass = proxyClass;
		
			this.proxy = (T) Proxy.newProxyInstance(proxyClass.getClassLoader(), new Class[] {proxyClass} , this);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

			proxyInfo = new MethodInvocation<T, E>(proxyClass, method, args);

			return null;
		}
		
		public T getProxy(){
			return proxy;
		}
		
		public MethodInvocation<T, E> getProxyInfo(){
			
			Objects.requireNonNull(proxyInfo, "Proxy method was never called.");
			return proxyInfo;
		}
	}
}