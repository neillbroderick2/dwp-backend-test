package gov.dwp.ms.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import gov.dwp.ms.model.APILocationResponse;
import gov.dwp.ms.model.APIUsersResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.springframework.context.annotation.Profile;


@Profile("test") // Set this class to only run when the test profile is active.
public class DwpMsApiTestBuilder {
	
	private List<APIUsersResponse> userList = new ArrayList<APIUsersResponse>();
	private List<APIUsersResponse> searchList = new ArrayList<APIUsersResponse>();
	private APILocationResponse location = new APILocationResponse();
	private Set<APIUsersResponse> resultSet = new HashSet<APIUsersResponse>(); 

	public String getExpectedResults() throws Exception { // Get a sorted list of results for matching purposes.
		List<APIUsersResponse> expectedResults = new ArrayList<APIUsersResponse>(resultSet); // Convert the set into a list.
        Collections.sort(expectedResults, new DwpMsUserSort()); // Sort using the same method as main app to ensure consistency.
		
		return getJsonString(expectedResults);
	}
	
	public CompletableFuture<List<APIUsersResponse>> getUsersResponse() { // Returns a completed future mock users api response.
		return CompletableFuture.completedFuture(userList);
	}
	
	public CompletableFuture<List<APIUsersResponse>> getSearchResponse() { // Returns a completed future mock search api response.
		return CompletableFuture.completedFuture(searchList);
	}
	
	public CompletableFuture<APILocationResponse> getLocationResponse() { // Returns a completed future mock location api response.
		return CompletableFuture.completedFuture(location);
	}
	
	public void setUsersResponse(int id, String firstName, String lastName, String email, String ipAddress, double latitude, double longitude, boolean isResult) { // Creates and adds a user api response each time it is called.
		APIUsersResponse response = new APIUsersResponse();
		response.setId(id);
		response.setFirst_name(firstName);
		response.setLast_name(lastName);
		response.setEmail(email);
		response.setIp_address(ipAddress);
		response.setLatitude(latitude);
		response.setLongitude(longitude); // Set object with passed parameters.
		
		userList.add(response); // Add to user list as a return value.
		
		if (isResult) {
			resultSet.add(response); // If this entry has been marked as a positive result, add it to the result set as well.
		}
	}
	
	public void setSearchResponse(int id, String firstName, String lastName, String email, String ipAddress, double latitude, double longitude) { // Creates and adds a search api response each time it is called.
		APIUsersResponse response = new APIUsersResponse();
		response.setId(id);
		response.setFirst_name(firstName);
		response.setLast_name(lastName);
		response.setEmail(email);
		response.setIp_address(ipAddress);
		response.setLatitude(latitude);
		response.setLongitude(longitude);
		
		searchList.add(response);
		resultSet.add(response); // Search responses get added to both the set and list by default as this is behaviour in the main app.
	}
	
	public void setLocationResponse(String name, String lat, String lon) { // Creates a location response when called.
	    location.setDisplay_name(name);
	    location.setLat(lat);
	    location.setLon(lon);
	}
	
	private <T> String getJsonString(T obj) throws Exception { // Helper method to convert objects to strings for result matching.
    	ObjectMapper mapper = new ObjectMapper();
    	
    	return mapper.writeValueAsString(obj);
    }
}
