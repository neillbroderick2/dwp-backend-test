package gov.dwp.ms.service;


import gov.dwp.ms.model.APIUsersResponse;
import gov.dwp.ms.model.APILocationResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;


@Service
public interface DwpMsService {

    CompletableFuture<APILocationResponse> getLocationResults(String city) throws Exception;
    
    CompletableFuture<List<APIUsersResponse>> getUsersResults() throws Exception;
    
    CompletableFuture<List<APIUsersResponse>> getSearchResults(String city) throws Exception;
    
    void setRequestId(String xCorrelationId);
    
    String getRequestId() throws Exception;
}
