# rest-template-client
Java 8 Spring RestTemplate client - Based on builders and dynamic proxies

```java
public class TestClass {
	public static void main(String[] args) {
		
		RestTemplateClient restClient = RestTemplateClient.host("http://localhost:8080")
		/*			.andRestTemplate(new RestTemplate())*/;

	// Sending request to 'simpleEmptyService':
		Interfaces.methodCall(UsersController.class, UsersController::simpleEmptyService)
				.map(restClient::Request)
		//			.withUriVariables(new Object[]{})
		//			.withHeader("Accept", "application/json")
		//			.withBody("Sample body content")
				.send();
		
	// Requesting a 2nd service using a Lambda function:
		String returnValue = Interfaces.methodCall(UsersController.class, controller -> controller.helloService("hola"))
			.map(restClient::Request)
			.sendGet(String.class);
	
		System.out.println(returnValue);
	}
}

@RequestMapping("/users")
public interface UsersController{
	
	@RequestMapping
	void simpleEmptyService();
	
	@RequestMapping("/hello/{hello}")
	String helloService(@PathVariable("hello") String helloValue);
}
