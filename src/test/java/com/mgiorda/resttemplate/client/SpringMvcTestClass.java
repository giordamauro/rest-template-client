package com.mgiorda.resttemplate.client;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.mgiorda.resttemplate.Application;
import com.mgiorda.resttemplate.client.mvc.SpringMvcTemplateClient;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@IntegrationTest("server.port=8080")
@SpringApplicationConfiguration(classes = Application.class)
public class SpringMvcTestClass {

	private SpringMvcTemplateClient restClient = RestTemplateClient.host("http://localhost:8080")
						.addDefaultHeader("Authorization", "Basic dXNlcm5hbWU6MTIzNDU2")
						.setRestTemplate(new TestRestTemplate())
						.map(SpringMvcTemplateClient::new);
		
	@Test
	public void testRequestSimpleService(){

		ResponseEntity<?> responseEntity = restClient.getVoidResponseEntity(UserController.class, UserController::simpleEmptyService);
		
		Assert.assertThat(responseEntity.getStatusCode(), Matchers.equalTo(HttpStatus.OK));
	}

	@Test
	public void testRequestServiceUsingLambda(){
		
		String pathValue = "helloValue";
		String queryValue = "queryValue";
		
		String returnValue = restClient.sendAndGet(UserController.class, controller -> controller.helloService(pathValue, queryValue));

		String expectedMessage = String.format(UserControlerTestImpl.TEST_HELLO_SERVICE_FORMAT, pathValue, queryValue);
		Assert.assertThat(returnValue, Matchers.equalTo(expectedMessage));
	}
	
	@Test
	public void testOverridingHttpValues(){
		
		String pathValue = "helloValue";
		String queryValue = "queryValue";
		
		ResponseEntity<Integer> responseEntity = restClient.newRequest(UserController.class, controller -> controller.helloService(pathValue, queryValue))
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
