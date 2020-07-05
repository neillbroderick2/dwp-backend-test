package gov.dwp.ms.service;


import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.dwp.ms.model.APILocationResponse;
import gov.dwp.ms.model.APIUsersResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;


@ActiveProfiles("test")
@SpringBootTest(classes = DwpMsServiceImplTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DwpMsServiceImplTest {
	
    @Mock
    private RestTemplate mockApi; // Mock the rest template so we can inject responses.
 
    @InjectMocks
    private DwpMsServiceImpl dwpService = new DwpMsServiceImpl();
       
    private String usersUrl = "https://testusers.com";
    private String locationIqUrl = "https://testlocation.com/v1/search.php";
    private String locationIqToken = "test_token";
    private String locationErrorMsg = "Unable to obtain city from location api.";
    private String usersErrorMsg = "Unable to obtain users from users api."; // Setup defaults for values usually taken from app.props.
    
    @Before
    public void beforeTest() {
    	dwpService.usersUrl = this.usersUrl;
    	dwpService.locationIqUrl = this.locationIqUrl;
    	dwpService.locationIqToken = this.locationIqToken;
    	dwpService.locationErrorMsg = this.locationErrorMsg;
    	dwpService.usersErrorMsg = this.usersErrorMsg; // Set the class with the variables ready for testing.
    	dwpService.setRequestId("client-xyz-id-12345");
    }

    @Test
    public void getLocationResults_successWithResults() throws Exception { // Test getLocationResults call is a success and returns results.
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474"); // Build the response objects used in the test.
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(locationIqUrl) 
		        .queryParam("key", locationIqToken)
		        .queryParam("q", "London")
		        .queryParam("format", "json"); // Build the url in the same manner as the main app.   
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APILocationResponse[].class))
        		.thenReturn(new ResponseEntity<APILocationResponse[]>(dwpTestBuilder.getLocationResponse(), HttpStatus.OK)); // Set the expected api call with our mock response.
 
        CompletableFuture<APILocationResponse> location = dwpService.getLocationResults("London"); // Run the test.
        
        assertEquals(dwpTestBuilder.getLocationResponse()[0],  location.get()); // Run assertions to ensure match.
    }
    
    @Test
    public void getUsersResults_successWithResults() throws Exception { // Test getUsersResults call is a success and returns results.
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(usersUrl)
    			.path("users");    
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APIUsersResponse[].class))
        		.thenReturn(new ResponseEntity<APIUsersResponse[]>(dwpTestBuilder.getUsersResponse(), HttpStatus.OK));
 
        CompletableFuture<List<APIUsersResponse>> users = dwpService.getUsersResults();
        
        assertEquals(Arrays.asList(dwpTestBuilder.getUsersResponse()),  users.get());
    }
    
    @Test
    public void getSearchResults_successWithResults() throws Exception { // Test getSearchResults call is a success and returns results.
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setSearchResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497);
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(usersUrl)
    			.path("/city")
    			.path("/London")
    			.path("/users");  
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APIUsersResponse[].class))
        		.thenReturn(new ResponseEntity<APIUsersResponse[]>(dwpTestBuilder.getSearchResponse(), HttpStatus.OK));
 
        CompletableFuture<List<APIUsersResponse>> search = dwpService.getSearchResults("London");

        assertEquals(Arrays.asList(dwpTestBuilder.getSearchResponse()),  search.get());
    }
    
    @Test
    public void getLocationResults_successNoResults() throws Exception { // Test getLocationResults call is a success with no results.
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(locationIqUrl) 
		        .queryParam("key", locationIqToken)
		        .queryParam("q", "London")
		        .queryParam("format", "json");    
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APILocationResponse[].class))
        		.thenReturn(new ResponseEntity<APILocationResponse[]>(dwpTestBuilder.getLocationResponse(), HttpStatus.OK));
 
        CompletableFuture<APILocationResponse> location = dwpService.getLocationResults("London");
        
        assertEquals(dwpTestBuilder.getLocationResponse()[0],  location.get());
    }
    
    @Test
    public void getUsersResults_successNoResults() throws Exception { // Test getUsersResults call is a success with no results.
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(usersUrl)
    			.path("users");    
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APIUsersResponse[].class))
        		.thenReturn(new ResponseEntity<APIUsersResponse[]>(dwpTestBuilder.getUsersResponse(), HttpStatus.OK));
 
        CompletableFuture<List<APIUsersResponse>> users = dwpService.getUsersResults();
        
        assertEquals(Arrays.asList(dwpTestBuilder.getUsersResponse()),  users.get());
    }
    
    @Test
    public void getSearchResults_successNoResults() throws Exception { // Test getSearchResults call is a success with no results.
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(usersUrl)
    			.path("/city")
    			.path("/London")
    			.path("/users");    
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APIUsersResponse[].class))
    			.thenReturn(new ResponseEntity<APIUsersResponse[]>(dwpTestBuilder.getSearchResponse(), HttpStatus.OK));
 
        CompletableFuture<List<APIUsersResponse>> search = dwpService.getSearchResults("London");

        assertEquals(Arrays.asList(dwpTestBuilder.getSearchResponse()),  search.get());
    }
    
    @Test(expected = ResponseStatusException.class) // The test will pass if this exception is thrown
    public void getLocationResults_clientException() throws Exception { // Test getLocationResults throws ResponseStatusException on client error. 
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(locationIqUrl) 
		        .queryParam("key", locationIqToken)
		        .queryParam("q", "London")
		        .queryParam("format", "json");
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APILocationResponse.class)).thenThrow(HttpClientErrorException.class);
    	
    	@SuppressWarnings("unused")
    	CompletableFuture<APILocationResponse> location = dwpService.getLocationResults("London");
    }
    
    @Test(expected = ResponseStatusException.class)
    public void getUsersResults_clientException() throws Exception { // Test getUsersResults throws ResponseStatusException on client error.
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(usersUrl)
    			.path("users");
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APIUsersResponse.class)).thenThrow(HttpClientErrorException.class);
    	
    	@SuppressWarnings("unused")
    	CompletableFuture<List<APIUsersResponse>> location = dwpService.getUsersResults();
    }
    
    @Test(expected = ResponseStatusException.class)
    public void getSearchResults_clientException() throws Exception { // Test getSearchResults throws ResponseStatusException on client error.
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setSearchResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497);
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(usersUrl)
    			.path("/city")
    			.path("/London")
    			.path("/users"); 
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APIUsersResponse.class)).thenThrow(HttpClientErrorException.class);
    	
    	@SuppressWarnings("unused")
    	CompletableFuture<List<APIUsersResponse>> location = dwpService.getSearchResults("London");
    }
    
    @Test(expected = ResponseStatusException.class)
    public void getLocationResults_serverException() throws Exception { // Test getLocationResults throws ResponseStatusException on server error. 
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(locationIqUrl) 
		        .queryParam("key", locationIqToken)
		        .queryParam("q", "London")
		        .queryParam("format", "json");
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APILocationResponse.class)).thenThrow(HttpClientErrorException.class);
    	
    	@SuppressWarnings("unused")
        CompletableFuture<APILocationResponse> location = dwpService.getLocationResults("London");
    }
    
    @Test(expected = ResponseStatusException.class)
    public void getUsersResults_serverException() throws Exception { // Test getUsersResults throws ResponseStatusException on server error.
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(usersUrl)
    			.path("users");
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APIUsersResponse.class)).thenThrow(HttpServerErrorException.class);
    	
    	@SuppressWarnings("unused")
    	CompletableFuture<List<APIUsersResponse>> location = dwpService.getUsersResults();
    }
    
    @Test(expected = ResponseStatusException.class)
    public void getSearchResults_serverException() throws Exception { // Test getSearchResults throws ResponseStatusException on server error.
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setSearchResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497);
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(usersUrl)
    			.path("/city")
    			.path("/London")
    			.path("/users");     
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APIUsersResponse.class)).thenThrow(HttpServerErrorException.class);
    	
    	@SuppressWarnings("unused")
    	CompletableFuture<List<APIUsersResponse>> location = dwpService.getSearchResults("London");
    }
    
    @Test(expected = Exception.class)
    public void getLocationResults_fatalException() throws Exception { // Test getLocationResults throws ResponseStatusException on fatal error.
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(locationIqUrl) 
		        .queryParam("key", locationIqToken)
		        .queryParam("q", "London")
		        .queryParam("format", "json");
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APILocationResponse.class)).thenThrow(Exception.class);
    	
    	@SuppressWarnings("unused")
    	CompletableFuture<APILocationResponse> location = dwpService.getLocationResults("London");
    }
    
    @Test(expected = Exception.class)
    public void getUsersResults_fatalException() throws Exception { // Test getUsersResults throws ResponseStatusException on fatal error.
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(usersUrl)
    			.path("users");
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APIUsersResponse.class)).thenThrow(Exception.class);
    	
    	@SuppressWarnings("unused")
    	CompletableFuture<List<APIUsersResponse>> location = dwpService.getUsersResults();
    }
    
    @Test(expected = Exception.class)
    public void getSearchResults_fatalException() throws Exception { // Test getLocationResults throws ResponseStatusException on fatal error.
    	DwpMsServiceTestBuilder dwpTestBuilder = new DwpMsServiceTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setSearchResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497);
	    
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(usersUrl)
    			.path("/city")
    			.path("/London")
    			.path("/users");     
        
    	Mockito.when(mockApi.getForEntity(builder.build().encode().toUri(), APIUsersResponse.class)).thenThrow(Exception.class);
    	
    	@SuppressWarnings("unused")
    	CompletableFuture<List<APIUsersResponse>> location = dwpService.getSearchResults("London");
    }
}
