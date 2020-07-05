package gov.dwp.ms.api;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.dwp.ms.service.DwpMsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.server.ResponseStatusException;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // Set to a random port to ensure there are no port clashes when testing.
@ActiveProfiles("test") // Set this class to only run when the test profile is active.
@RunWith(SpringJUnit4ClassRunner.class)
public class DwpMsApiApplicationTest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
    private DwpMsService dwpMsService;
	
	@Autowired
	private TestRestTemplate mockApi;
	
	@Test
    public void locationByRadius_cityPathVariableMaxLength() throws Exception { // Test the city path variable errors if length is over 50 chars.
    	ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/Londonasdasdasasdasdasasdasdasdasdasdasdasasdadasdasdasasdas/radius/50/users", String.class);
        
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, actualResult.getStatusCode());
    }
    
    @Test
    public void locationByRadius_radiusPathVariableMinLength() throws Exception { // Test the radius path variable errors if min is less than 1.
    	ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/0/users", String.class);
        
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, actualResult.getStatusCode());
    }
    
    @Test
    public void locationByRadius_radiusPathVariableMaxLength() throws Exception { // Test the radius path variable errors if max is less than 1.
    	ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/101/users", String.class);
        
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, actualResult.getStatusCode());
    }
	 
    @Test
    public void londonFiftyMileRadius_successWithResults() throws Exception { // Test londonFiftyMileRadius call is a success and returns results.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true); // Build the response objects used in the test.
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse()); // Set the expected service calls to respond with the object provided.
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
   
		String expectedResult = dwpTestBuilder.getExpectedResults(); // Get the expected result from the builder.
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/", String.class);
        
        assertSame(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(expectedResult, actualResult.getBody()); // Run assertions to ensure matches
    }
    
    @Test
    public void locationByRadius_successWithResults() throws Exception { // Test locationByRadius call is a success and returns results.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        Mockito.when(dwpMsService.getLocationResults("London")).thenReturn(dwpTestBuilder.getLocationResponse());

        String expectedResult = dwpTestBuilder.getExpectedResults();
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        
        assertSame(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(expectedResult, actualResult.getBody());
    }
    
    @Test
    public void locationByRadius_successWithoutResults() throws Exception { // Test locationByRadius call is a success and returns no content status.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setLocationResponse("Manchester", "53.4794892", "-2.2451148");
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, false);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, false);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, false);
    	
        Mockito.when(dwpMsService.getSearchResults("Manchester")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        Mockito.when(dwpMsService.getLocationResults("Manchester")).thenReturn(dwpTestBuilder.getLocationResponse());

        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/Manchester/radius/50/users", String.class);
        
        assertSame(HttpStatus.NO_CONTENT, actualResult.getStatusCode());
    }
    
    @Test
    public void londonFiftyMileRadius_correlationIdGenerated() throws Exception { // Test londonFiftyMileRadius call generates a correlation id if one does not exist.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());

        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/", String.class);
        HttpHeaders resultHeaders = actualResult.getHeaders();

        assertNotNull(resultHeaders.get("X-Correlation-Id"));
    }
    
    @Test
    public void locationByRadius_correlationIdGenerated() throws Exception { // Test locationByRadius call generates a correlation id if one does not exist.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
    	Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        Mockito.when(dwpMsService.getLocationResults("London")).thenReturn(dwpTestBuilder.getLocationResponse());

        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        HttpHeaders resultHeaders = actualResult.getHeaders();

        assertNotNull(resultHeaders.get("X-Correlation-Id"));
    }
    
    @Test
    public void londonFiftyMileRadius_requesterCorrelationIdUsed() throws Exception { // Test londonFiftyMileRadius call uses the requester correlation id if one is provided.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        
        HttpHeaders headers = new HttpHeaders();
		headers.add("X-Correlation-Id", "client-xyz-id-12345");
        
        ResponseEntity<String> actualResult = mockApi.exchange("http://localhost:" + port + "/", HttpMethod.GET, new HttpEntity<Object>(headers), String.class);
        HttpHeaders resultHeaders = actualResult.getHeaders();

        assertEquals(headers.get("X-Correlation-Id").toString(), resultHeaders.get("X-Correlation-Id").toString());
    }
    
    @Test
    public void locationByRadius_requesterCorrelationIdUsed() throws Exception { // Test locationByRadius call uses the requester correlation id if one is provided.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
    	Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        Mockito.when(dwpMsService.getLocationResults("London")).thenReturn(dwpTestBuilder.getLocationResponse());
        
        HttpHeaders headers = new HttpHeaders();
		headers.add("X-Correlation-Id", "client-xyz-id-12345");
        
        ResponseEntity<String> actualResult = mockApi.exchange("http://localhost:" + port + "/city/London/radius/50/users", HttpMethod.GET, new HttpEntity<Object>(headers), String.class);
        HttpHeaders resultHeaders = actualResult.getHeaders();

        assertEquals(headers.get("X-Correlation-Id").toString(), resultHeaders.get("X-Correlation-Id").toString());
    }

    @Test
    public void londonFiftyMileRadius_userApiNoResults() throws Exception { // Test londonFiftyMileRadius call errors if there are no user api results.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);

        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/", String.class);
        
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, actualResult.getStatusCode());
    }
    
    @Test
    public void locationByRadius_userApiNoResults() throws Exception { // Test locationByRadius call errors if there are no user api results.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);

    	Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        Mockito.when(dwpMsService.getLocationResults("London")).thenReturn(dwpTestBuilder.getLocationResponse());
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, actualResult.getStatusCode());
    }
    
    @Test
    public void londonFiftyMileRadius_searchApiNoResults() throws Exception { // Test londonFiftyMileRadius call errors if there are no search api results.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);

        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        
        String expectedResult = dwpTestBuilder.getExpectedResults();
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/", String.class);
        
        assertSame(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(expectedResult, actualResult.getBody());    
    }
    
    @Test
    public void locationByRadius_searchApiNoResults() throws Exception { // Test locationByRadius call errors if there are no search api results.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);

    	Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        Mockito.when(dwpMsService.getLocationResults("London")).thenReturn(dwpTestBuilder.getLocationResponse());
        
        String expectedResult = dwpTestBuilder.getExpectedResults();
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        
        assertSame(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(expectedResult, actualResult.getBody());
    }
    
    @Test
    public void locationByRadius_locationApiNoResults() throws Exception { // Test locationByRadius call errors if there are no location api results.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);

    	Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        Mockito.when(dwpMsService.getLocationResults("London")).thenReturn(dwpTestBuilder.getLocationResponse());
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext // The next set of tests throw exceptions which dirties the spring context, so we will refresh the context before each test.
    public void londonFiftyMileRadius_userApiClientException() throws Exception { // Test londonFiftyMileRadius call when a client exception occurs on user api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/", String.class);
        
        assertSame(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void locationByRadius_userApiClientException() throws Exception { // Test locationByRadius call when a client exception occurs on user api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        Mockito.when(dwpMsService.getLocationResults("London")).thenReturn(dwpTestBuilder.getLocationResponse());
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        
        assertSame(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void londonFiftyMileRadius_userApiServerException() throws Exception { // Test londonFiftyMileRadius call when a server exception occurs on user api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenThrow(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE));
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/", String.class);
        
        assertSame(HttpStatus.SERVICE_UNAVAILABLE, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void locationByRadius_userApiServerException() throws Exception { // Test locationByRadius call when a server exception occurs on user api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenThrow(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE));
        Mockito.when(dwpMsService.getLocationResults("London")).thenReturn(dwpTestBuilder.getLocationResponse());
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        
        assertSame(HttpStatus.SERVICE_UNAVAILABLE, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void londonFiftyMileRadius_userApiFatalException() throws Exception { // Test londonFiftyMileRadius call when a fatal exception occurs on user api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/", String.class);
        
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void locationByRadius_userApiFatalException() throws Exception { // Test locationByRadius call when a fatal exception occurs on user api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        Mockito.when(dwpMsService.getLocationResults("London")).thenReturn(dwpTestBuilder.getLocationResponse());
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void londonFiftyMileRadius_searchApiClientException() throws Exception { // Test londonFiftyMileRadius call when a client exception occurs on search api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/", String.class);
        
        assertSame(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void locationByRadius_searchApiClientException() throws Exception { // Test locationByRadius call when a client exception occurs on search api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
    	Mockito.when(dwpMsService.getSearchResults("London")).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        Mockito.when(dwpMsService.getLocationResults("London")).thenReturn(dwpTestBuilder.getLocationResponse());
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        
        assertSame(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void londonFiftyMileRadius_searchApiServerException() throws Exception { // Test londonFiftyMileRadius call when a server exception occurs on search api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
    	Mockito.when(dwpMsService.getSearchResults("London")).thenThrow(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE));
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/", String.class);
        
        assertSame(HttpStatus.SERVICE_UNAVAILABLE, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void locationByRadius_searchApiServerException() throws Exception { // Test locationByRadius call when a server exception occurs on search api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
    	Mockito.when(dwpMsService.getSearchResults("London")).thenThrow(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE));
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        Mockito.when(dwpMsService.getLocationResults("London")).thenReturn(dwpTestBuilder.getLocationResponse());
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        
        assertSame(HttpStatus.SERVICE_UNAVAILABLE, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void londonFiftyMileRadius_searchApiFatalException() throws Exception { // Test londonFiftyMileRadius call when a fatal exception occurs on search api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
    	Mockito.when(dwpMsService.getSearchResults("London")).thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/", String.class);
        
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void locationByRadius_searchApiFatalException() throws Exception { // Test locationByRadius call when a fatal exception occurs on search api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setLocationResponse("London", "51.5073219", "-0.1276474");
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
    	Mockito.when(dwpMsService.getSearchResults("London")).thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        Mockito.when(dwpMsService.getLocationResults("London")).thenReturn(dwpTestBuilder.getLocationResponse());
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void locationByRadius_locationApiClientException() throws Exception { // Test locationByRadius call when a client exception occurs on location api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        Mockito.when(dwpMsService.getLocationResults("London")).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        
        assertSame(HttpStatus.NOT_FOUND, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void locationByRadius_locationApiServerException() throws Exception { // Test locationByRadius call when a server exception occurs on location api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        Mockito.when(dwpMsService.getLocationResults("London")).thenThrow(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE));
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        
        assertSame(HttpStatus.SERVICE_UNAVAILABLE, actualResult.getStatusCode());
    }
    
    @Test
    @DirtiesContext
    public void locationByRadius_locationApiFatalException() throws Exception { // Test locationByRadius call when a fatal exception occurs on location api.
    	DwpMsApiTestBuilder dwpTestBuilder = new DwpMsApiTestBuilder();
    	dwpTestBuilder.setSearchResponse(135, "Mechelle", "Boam", "test@test.com", "113.71.242.187", -6.5115909, 105.652983);
    	dwpTestBuilder.setUsersResponse(136, "Ancell", "Garnsworthy", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(137, "Phyllys", "Hebbs", "test@test.com", "113.71.242.187", -6.7098551, 111.3479498, false);
    	dwpTestBuilder.setUsersResponse(138, "Stephen", "Mapstone", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	dwpTestBuilder.setUsersResponse(139, "Hugo", "Lynd", "test@test.com", "113.71.242.187", -6.5115909, 105.652983, false);
    	dwpTestBuilder.setUsersResponse(140, "Terry", "Stowgill", "test@test.com", "113.71.242.187", 51.5489435, 0.3860497, true);
    	
        Mockito.when(dwpMsService.getSearchResults("London")).thenReturn(dwpTestBuilder.getSearchResponse());
        Mockito.when(dwpMsService.getUsersResults()).thenReturn(dwpTestBuilder.getUsersResponse());
        Mockito.when(dwpMsService.getLocationResults("London")).thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        
        ResponseEntity<String> actualResult = mockApi.getForEntity("http://localhost:" + port + "/city/London/radius/50/users", String.class);
        
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, actualResult.getStatusCode());
    }
}
