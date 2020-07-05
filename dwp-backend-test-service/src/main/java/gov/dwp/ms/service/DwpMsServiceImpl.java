package gov.dwp.ms.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import gov.dwp.ms.model.APIUsersResponse;
import gov.dwp.ms.model.APILocationResponse;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class DwpMsServiceImpl implements DwpMsService {
	
	private static Logger log = LoggerFactory.getLogger(DwpMsServiceImpl.class);
	
  	@Autowired
  	RestTemplate restTemplate;
  	
  	@Value("${usersApi.url}")
  	public String usersUrl;
  	
  	@Value("${locationIq.url}")
    public String locationIqUrl;
  	
  	@Value("${locationIq.token}")
  	public String locationIqToken;
  	
  	@Value("${error.location.api}")
  	public String locationErrorMsg;
  	
  	@Value("${error.users.api}")
  	public String usersErrorMsg;
  	
  	private String xCorrelationId;
  	
    public String getRequestId() throws Exception {
    	return xCorrelationId;
    }
    
    public void setRequestId(String xCorrelationId) {
    	this.xCorrelationId = xCorrelationId;
    }
  	
    @Retryable( // Allows the method to be retried on a server error (5xx)
		  value = {HttpServerErrorException.class}, // Exception name we want to retry when thrown (can add addition classes).
		  maxAttempts = 3, // Number of retries.
		  backoff = @Backoff(delay = 500)) // Delay between retries.
  	@Cacheable("cities") // This methods results will be cached for faster retrieval. For city co-ords this is fine as they will never change.
    @Async("dwpTaskExec") // Assigns this method to the dwpTaskExec TaskExecutor thread pool.
    public CompletableFuture<APILocationResponse> getLocationResults(String city) throws Exception { // Gets location results from LocationIq web service.
    	try {
	    	log.debug(xCorrelationId + ": Starting getLocationResults method");
	    	log.debug("location: " + locationIqUrl);
	    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(locationIqUrl) 
			        .queryParam("key", locationIqToken)
			        .queryParam("q", StringUtils.capitalize(city))
			        .queryParam("format", "json");    	    	
	    	URI url = builder.build().encode().toUri(); // Build the request url.
	    	
	    	log.debug(xCorrelationId + ": Getting results from " + url.toString());
	    	
	    	ResponseEntity<APILocationResponse[]> response = restTemplate.getForEntity(url, APILocationResponse[].class); // Call the api and serialise results into response object array.
	    	
	    	log.debug(xCorrelationId + ": Status code " + response.getStatusCodeValue());
	    	
	    	if (response.getBody() == null) {
	    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, locationErrorMsg); // Shouldn't get here, but sanity check anyway to prevent null pointer.
	    	}
			
	    	APILocationResponse firstResult = new APILocationResponse();
	    	firstResult = response.getBody()[0]; // Use the first result as this is the top result match.
	    	
	    	log.debug(xCorrelationId + ": City match found " + getJsonString(firstResult));
	    	log.debug(xCorrelationId + ": Finishing getLocationResults method");

	    	return CompletableFuture.completedFuture(firstResult);
	    	
    	} catch(HttpClientErrorException e) { // Check for any errors in the api call
    		log.error(xCorrelationId + ": Client Exception getLocationResults method " + e.getMessage());

    		throw new ResponseStatusException(e.getStatusCode()); // Throw client errors (4xx) back to the requester.
    		
    	} catch(HttpServerErrorException e) {
    		log.error(xCorrelationId + ": Server Exception getLocationResults method " + e.getMessage());

    		throw new ResponseStatusException(e.getStatusCode()); // Throw server errors (5xx) back to the requester.
    		
    	} catch(Exception e) { 
    		log.error(xCorrelationId + ": Exception getLocationResults method " + e.getMessage());

    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, locationErrorMsg); // Throw all other errors back to the requester with a friendly error message.
    	}
    }
    
    @Retryable(
  		  value = {HttpServerErrorException.class}, 
  		  maxAttempts = 3,
  		  backoff = @Backoff(delay = 500))
    @Async("dwpTaskExec") 
    public CompletableFuture<List<APIUsersResponse>> getUsersResults() throws Exception { // Gets users list from users api.
    	try {
    		log.debug(xCorrelationId + ": Starting getUsersResults method");
    		
	    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(usersUrl)
	    			.path("users");
	    	URI url = builder.build().encode().toUri();
	    	
	    	log.debug(xCorrelationId + ": Getting results from " + url.toString());
	    	
	    	ResponseEntity<APIUsersResponse[]> response = restTemplate.getForEntity(url, APIUsersResponse[].class);
			
	    	log.debug(xCorrelationId + ": Status code " + response.getStatusCodeValue());
	    	
	    	if (response.getBody() == null) {
	    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, usersErrorMsg); // Shouldn't get here, but sanity check anyway to prevent null pointer.
	    	}
    		
	    	List<APIUsersResponse> results = Arrays.asList(response.getBody()); // Convert results array to a list.
	    	
	    	log.debug(xCorrelationId + ": Users found " + getJsonString(results.get(0))); // Limited to first entry but could log whole response.
	    	log.debug(xCorrelationId + ": Finishing getUsersResults method");

	    	return CompletableFuture.completedFuture(results);
	    	
    	} catch(HttpClientErrorException e) {
    		log.error(xCorrelationId + ": Client Exception getUsersResults method " + e.getMessage());

    		throw new ResponseStatusException(e.getStatusCode());
    		
    	} catch(HttpServerErrorException e) {
    		log.error(xCorrelationId + ": Server Exception getUsersResults method " + e.getMessage());

    		throw new ResponseStatusException(e.getStatusCode());
    		
    	} catch(Exception e) { 
    		log.error(xCorrelationId + ": Exception getUsersResults method " + e.getMessage());

    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, usersErrorMsg);
    	}
    }
    
    @Retryable(
  		  value = {HttpServerErrorException.class}, 
  		  maxAttempts = 3,
  		  backoff = @Backoff(delay = 500))
    @Async("dwpTaskExec") 
    public CompletableFuture<List<APIUsersResponse>> getSearchResults(String city) throws Exception { // Gets search results from users api. 	
    	try {
    		log.debug(xCorrelationId + ": Starting getSearchResults method");
    		
	    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(usersUrl)
	    			.path("city/")
	    			.path(StringUtils.capitalize(city))
	    			.path("/users");
	    	URI url = builder.build().encode().toUri();
	    	
	    	log.debug(xCorrelationId + ": Getting results from " + url.toString());
	    	
	    	ResponseEntity<APIUsersResponse[]> response = restTemplate.getForEntity(url, APIUsersResponse[].class);
	    	
	    	if (response.getBody() == null) {
	    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, usersErrorMsg); // Shouldn't get here, but sanity check anyway to prevent null pointer.
	    	}
	    	
	    	log.debug(xCorrelationId + ": Status code " + response.getStatusCodeValue());
    		
	    	List<APIUsersResponse> results = Arrays.asList(response.getBody());
	    	
	    	log.debug(xCorrelationId + ": Users found " + getJsonString(results));
	    	log.debug(xCorrelationId + ": Finishing getSearchResults method");

	    	return CompletableFuture.completedFuture(results);
	    	
    	} catch(HttpClientErrorException e) {
    		log.error(xCorrelationId + ": Client Exception getSearchResults method " + e.getMessage());

    		throw new ResponseStatusException(e.getStatusCode());
    		
    	} catch(HttpServerErrorException e) {
    		log.error(xCorrelationId + ": Server Exception getSearchResults method " + e.getMessage());

    		throw new ResponseStatusException(e.getStatusCode());
    		
    	} catch(Exception e) { 
    		log.error(xCorrelationId + ": Exception getSearchResults method " + e.getMessage());

    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, usersErrorMsg);
    	}
    }

    private <T> String getJsonString(T obj) throws Exception { // Converts an object into a json string.
    	ObjectMapper mapper = new ObjectMapper();
    	
    	return mapper.writeValueAsString(obj);
    }
}
