package gov.dwp.ms.service;


import gov.dwp.ms.model.APILocationResponse;
import gov.dwp.ms.model.APIUsersResponse;
import org.springframework.context.annotation.Profile;


@Profile("test") // Set this class to only run when the test profile is active.
public class DwpMsServiceTestBuilder {
	
	private int counter = 0;
	private APILocationResponse[] locationResult = new APILocationResponse[10];
	private APIUsersResponse[] usersResult = new APIUsersResponse[10];
	private APIUsersResponse[] searchResult = new APIUsersResponse[10];

	public APIUsersResponse[] getUsersResponse() { // Returns a APIUsersResponse array mock users api response.
		return usersResult;
	}
	
	public APIUsersResponse[] getSearchResponse() { // Returns a APIUsersResponse array mock search api response.
		return searchResult;
	}
	
	public APILocationResponse[] getLocationResponse() { // Returns a APIUsersResponse array mock location api response.
		return locationResult;
	}
	
	public void setUsersResponse(int id, String firstName, String lastName, String email, String ipAddress, double latitude, double longitude, boolean isResult) { // Creates and adds a user api response each time it is called.
		APIUsersResponse response = new APIUsersResponse();
		response.setId(id);
		response.setFirst_name(firstName);
		response.setLast_name(lastName);
		response.setEmail(email);
		response.setIp_address(ipAddress);
		response.setLatitude(latitude);
		response.setLongitude(longitude);
		
		usersResult[counter] = response;
	    counter++;
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
		
		searchResult[counter] = response;
	    counter++;
	}
	
	public void setLocationResponse(String name, String lat, String lon) { // Creates and add a location api response each time it is called.
		APILocationResponse response = new APILocationResponse();
		response.setDisplay_name(name);
		response.setLat(lat);
		response.setLon(lon);
	    
	    locationResult[counter] = response;
	    counter++;
	}
}
