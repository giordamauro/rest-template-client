package com.mgiorda.resttemplate.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.function.Consumer;

public class Interfaces<T>{
			
	private final Class<T> proxyClass;
	
	public Interfaces(Class<T> proxyClass) {
		this.proxyClass = proxyClass;
	}
	
	public MethodInfo<T> method(Consumer<T> funcion){

		ProxyFactory<T> proxyFactory = new ProxyFactory<T>(proxyClass);
		
		funcion.accept(proxyFactory.getProxy());
		
		return proxyFactory.getProxyInfo();
	}
	
	public static <T> MethodInfo<T> methodCall(Class<T> interfaceClass, Consumer<T> consumer){
		
		return new Interfaces<T>(interfaceClass).method(consumer);
	}
		
	private static class ProxyFactory<T> implements InvocationHandler{

		private final Class<T> proxyClass;
		private final T proxy;
		
		private MethodInfo<T> proxyInfo;
		
		@SuppressWarnings("unchecked")
		public ProxyFactory(Class<T> proxyClass) {
			this.proxyClass = proxyClass;
		
			this.proxy = (T) Proxy.newProxyInstance(proxyClass.getClassLoader(), new Class[] {proxyClass} , this);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

			proxyInfo = new MethodInfo<T>(proxyClass, method, args);

			return null;
		}
		
		public T getProxy(){
			return proxy;
		}
		
		public MethodInfo<T> getProxyInfo(){
			
			Objects.requireNonNull(proxyInfo, "Proxy method was never called.");
			return proxyInfo;
		}
	}
}