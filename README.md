# rest-template-client
Java 8 Spring RestTemplate client - Based on builders and dynamic proxies

```java
@RequestMapping("/users")
public interface UsersController{
	
	@RequestMapping
	void simpleEmptyService();
	
	@RequestMapping("/hello/{hello}")
	String helloService(@PathVariable("hello") String helloValue);
}
```

```java
public class TestClass {
	private RestTemplateClient restClient = RestTemplateClient.host("http://localhost:8080")
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
		
		String returnValue = Interfaces.method(UsersController.class, controller -> controller.helloService("hola"))
				.map(SpringMvcRestService::New)
				.map(restClient::Request)
				.send();

		System.out.println(returnValue);
	}
	
	@Test
	public void testOverridingHttpValues(){
		
		ResponseEntity<Integer> responseEntity = restClient.Request(HttpMethod.PUT, "/nose")
				.withHttpMethod(HttpMethod.GET)
				.withServiceUrl("Overriden serviceUrl")
				.withUriVariables(new Object[] {"differentHola"})
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
```