package com.mgiorda.resttemplate.client;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.mgiorda.resttemplate.client.mvc.SpringMvcRestService;
import com.mgiorda.resttemplate.util.Interfaces;

public class TestClass {
	private RestTemplateClient restClient = RestTemplateClient.host("http://localhost:8080")
						.setDefaultHeader("Authorization", "Basic dXNlcm5hbWU6MTIzNDU2")
	/*					.andRestTemplate(new RestTemplate())*/;
		
	@Test
	public void testRequestSimpleService(){
		
		Interfaces.voidMethod(UsersController.class, UsersController::simpleEmptyService)
			.map(SpringMvcRestService::New)
			.map(restClient::Request)
			.send();
	}

	@Test
	public void testRequestServiceUsingLambda(){
		
		String returnValue = Interfaces.method(UsersController.class, controller -> controller.helloService("helloValue", "myQueryValue"))
				.map(SpringMvcRestService::New)
				.map(restClient::Request)
				.send();

		System.out.println(returnValue);
	}
	
	@Test
	public void testOverridingHttpValues(){
		
		ResponseEntity<Integer> responseEntity = restClient.Request(HttpMethod.PUT, "/dontknow")
				.withHttpMethod(HttpMethod.GET)
				.withServiceUrl("Overriden serviceUrl")
				.withUriVariables(new Object[] {"differentHola"})
				.withQueryParam("queryParam", "myValue")
				.withContentType(MediaType.APPLICATION_JSON)
				.withHeader("Accept", "application/json")
				.withBody("Sample body content")
				.withHttpHeaders(new HttpHeaders())
				.withRequestHttpEntity(new HttpEntity<String>("Another body"))
				.withResponseAs(Integer.class)
				.getResponseEntity();
		
		System.out.println(responseEntity.getStatusCode());
	}
}
