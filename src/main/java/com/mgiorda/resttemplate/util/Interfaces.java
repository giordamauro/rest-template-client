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
		this.proxyClass = proxyClass;
	}
	
	public MethodInfo<T, ?> consumer(Consumer<T> funcion){

		ProxyFactory<T, ?> proxyFactory = new ProxyFactory<>(proxyClass);
		
		funcion.accept(proxyFactory.getProxy());
		
		return proxyFactory.getProxyInfo();
	}
	
	public static <T> MethodInfo<T, ?> voidMethod(Class<T> interfaceClass, Consumer<T> consumer){
		
		return new Interfaces<T>(interfaceClass).consumer(consumer);
	}
	
	public static <T, R> MethodInfo<T, R> method(Class<T> interfaceClass, Function<T, R> consumer){
		return null;
	}
		
	private static class ProxyFactory<T, E> implements InvocationHandler{

		private final Class<T> proxyClass;
		private final T proxy;
		
		private MethodInfo<T, E> proxyInfo;
		
		@SuppressWarnings("unchecked")
		public ProxyFactory(Class<T> proxyClass) {
			this.proxyClass = proxyClass;
		
			this.proxy = (T) Proxy.newProxyInstance(proxyClass.getClassLoader(), new Class[] {proxyClass} , this);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

			proxyInfo = new MethodInfo<T, E>(proxyClass, method, args);

			return null;
		}
		
		public T getProxy(){
			return proxy;
		}
		
		public MethodInfo<T, E> getProxyInfo(){
			
			Objects.requireNonNull(proxyInfo, "Proxy method was never called.");
			return proxyInfo;
		}
	}
}