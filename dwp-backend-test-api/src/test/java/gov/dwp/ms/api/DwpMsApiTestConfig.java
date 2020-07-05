package gov.dwp.ms.api;


import gov.dwp.ms.service.DwpMsService;
import org.mockito.Mockito;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;


@Profile("test") // Set this class to only run when the test profile is active.
@Configuration
public class DwpMsApiTestConfig {
	
    @Bean
    @Primary
    public DwpMsService mockService() { // Bean to mock our service so we can mock service calls.
        return Mockito.mock(DwpMsService.class);
    }
	
    @Bean
    public TestRestTemplate mockApi() throws Exception { // Bean to setup test rest template so we can mock api calls.
		return new TestRestTemplate();
    }
}
