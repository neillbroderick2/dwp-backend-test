package gov.dwp.ms.api;


import gov.dwp.ms.model.APIUsersResponse;
import gov.dwp.ms.model.APILocationResponse;
import gov.dwp.ms.service.DwpMsService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@RequestMapping("/")
public class DwpMsRestApi {
    
    @Autowired
    DwpMsService dwpMsService;
    
    @Autowired
    DwpMsApiHelper dwpApiHelper;
    
    @Value("${default.latitutude}")
    private String defaultLatitude;
    
    @Value("${default.longitude}")
    private String defaultLongitude;
    
    @Value("${default.radius}")
    private int defaultRadius;
    
    private static Logger log = LoggerFactory.getLogger(DwpMsRestApi.class); // Setup a basic console logger.
    
    @GetMapping(
	        value = "/",
	        produces = {MediaType.APPLICATION_JSON_VALUE, "application/json"}
    )
    public ResponseEntity<?> londonFiftyMileRadius( // GET mapping for default call with no parameters. Will return users within 50 miles of London.
    		@RequestHeader(value = "X-Correlation-Id", required = false) String xCorrelationId
    ) throws Exception {
    	xCorrelationId = dwpApiHelper.checkRequestId(xCorrelationId); // Check to see if request id was provided and generate one if not.
    	dwpMsService.setRequestId(xCorrelationId); // Set the request id on the service so we can use it later.
    	
    	log.info(xCorrelationId + ": Starting londonFiftyMileRadius request");
        
        CompletableFuture<List<APIUsersResponse>> execSearch = dwpMsService.getSearchResults("London"); // Start futures for api calls. 
        CompletableFuture<List<APIUsersResponse>> execUsers = dwpMsService.getUsersResults();   
        CompletableFuture.allOf(execSearch, execUsers).join(); // Wait for the futures to finish.
         
        APILocationResponse location = new APILocationResponse();
        location.setLat(defaultLatitude);
        location.setLon(defaultLongitude);
        
        List<APIUsersResponse> response = dwpApiHelper.processResults(
        		defaultRadius,
        		location,
        		execSearch.get(),
        		execUsers.get()
		); // Process the future results to provide a filtered response based on default values.
        
        log.info(xCorrelationId + ": Ending londonFiftyMileRadius request");
        
        return new ResponseEntity<List<APIUsersResponse>>(response, dwpApiHelper.setResponseHeaders(xCorrelationId), HttpStatus.OK);
    }
    
    @GetMapping(
	        value = "city/{city}/radius/{radius}/users",
	        produces = {MediaType.APPLICATION_JSON_VALUE, "application/json"}
    )
    public ResponseEntity<?> locationByRadius( // GET mapping for with city and radius parameters passed.  
            @RequestHeader(value = "X-Correlation-Id", required = false) String xCorrelationId,
            @PathVariable(value = "city", required = true) @Size(max = 50) String city, // 50 char limit for city name.
            @PathVariable(value = "radius", required = true) @Min(1) @Max(100) int radius // Min 1 mile and max 100.
    ) throws Exception {
    	xCorrelationId = dwpApiHelper.checkRequestId(xCorrelationId); 
    	dwpMsService.setRequestId(xCorrelationId); 
    	
        log.info(xCorrelationId + ": Starting locationByRadius request");
        
        CompletableFuture<APILocationResponse> execLocation = dwpMsService.getLocationResults(city); 
        CompletableFuture<List<APIUsersResponse>> execSearch = dwpMsService.getSearchResults(city);
        CompletableFuture<List<APIUsersResponse>> execUsers = dwpMsService.getUsersResults(); 
        CompletableFuture.allOf(execLocation, execSearch, execUsers).join();
        
        List<APIUsersResponse> response = dwpApiHelper.processResults(
        		radius,
        		execLocation.get(),
        		execSearch.get(), 
        		execUsers.get()
		); // Process the future results to provide a filtered response based on source location and provided radius.
        
        log.info(xCorrelationId + ": Ending locationByRadius request");
        
        return new ResponseEntity<List<APIUsersResponse>>(response, dwpApiHelper.setResponseHeaders(xCorrelationId), HttpStatus.OK);
    }
}