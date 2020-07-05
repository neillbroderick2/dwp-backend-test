package gov.dwp.ms.api;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
public class DwpMsApiConfig {
	
    @Bean
    public Docket dwpApi() { // Bean to create Swagger documents from api calls. Can be accessed using https://localhost:8443/swagger-ui.html.
    	return new Docket(DocumentationType.SWAGGER_2)  
				.apiInfo(new ApiInfoBuilder()
				        .title("DWP Users API Microservice") // Swagger UI title & description below.
				        .description("Our API (Application Programming Interface) enables you to integrate with us and build applications that leverage our functionality and data.")
				        .version("1.0")
				        .build())
			    .select()                                  
			    .apis(RequestHandlerSelectors.any())              
			    .paths(PathSelectors.any()) // Selecting all paths, but can limit to specific apis using regex if required.                         
			    .build();
    }   
}
