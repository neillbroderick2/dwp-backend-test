package gov.dwp.ms.api;


import gov.dwp.ms.model.APIUsersResponse;
import java.util.Comparator;


public class DwpMsUserSort implements Comparator<APIUsersResponse> { 

    public int compare(APIUsersResponse a, APIUsersResponse b) { // Comparator for sorting by id.
        return a.getId() - b.getId(); 
    } 
} 
