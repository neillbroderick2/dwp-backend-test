package gov.dwp.ms.api;


import gov.dwp.ms.model.APILocationResponse;
import gov.dwp.ms.model.APIUsersResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;


@Component
public class DwpMsApiHelper {
	
	public List<APIUsersResponse> processResults(int radius, APILocationResponse location, List<APIUsersResponse> searchUsers, List<APIUsersResponse> radiusUsers) { // Processes lists of APIUserResponse against geodesic class to check if users within radius.	
		Set<APIUsersResponse> resultSet = new HashSet<APIUsersResponse>(); // Create a result set to contain the valid results.
		
		if (radiusUsers.isEmpty() || (location.getLat() == null) || (location.getLon() == null)) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR); // There should always be location / radiusUsers so throw an error to prevent null pointer.
		}
		
		if (!searchUsers.isEmpty()) {
			resultSet.addAll(searchUsers); // If there are some users from the city search result, add them to the result set as the city matched.
		}
		
		for (APIUsersResponse u : radiusUsers) { // Loop through radius api call users.
			GeodesicData result = Geodesic.WGS84.Inverse(Double.parseDouble(location.getLat()), Double.parseDouble(location.getLon()), u.getLatitude(), u.getLongitude()); // Get the geodesic data result, by passing the source lat/lon and users lat/lon.
			
		    double distanceInMiles = result.s12 / 1609.34; // Result is in meters so divide to convert it into miles.
		    	
		    if (distanceInMiles <= radius) { // If the distance is less than radius, add the user.
		    	resultSet.add(u); // Sets can only contain unique list values so this will prevent duplicates from the two api calls appearing in the results.
		    }
		}
		
		if (resultSet.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT); // If there are no results to display, throw a 204 NO CONTENT message back to the requester.
		}
		
		List<APIUsersResponse> response = new ArrayList<APIUsersResponse>(resultSet); 
		Collections.sort(response, new DwpMsUserSort()); // Sort the results by id before returning, so they are all nicely in the correct order.
		
		return response;
	}
	
	public String checkRequestId(String xCorrelationId) { // Checks if there is a request id passed by the requester & generates one if not.
		if (!StringUtils.isEmpty(xCorrelationId)) {
			return xCorrelationId; // If exists, just return the requesters id.
		}
		
		return UUID.randomUUID().toString(); // Otherwise generate a UUID and use that.
	}
	
	public HttpHeaders setResponseHeaders(String xCorrelationId) { // Sets the response headers, which in this case is the request id if one was provided.
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Correlation-Id", xCorrelationId);
		// Add additional response headers here...

		return headers;
	}
}