package gov.dwp.ms.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableSwagger2
@EnableAutoConfiguration
@ComponentScan(basePackages = {"gov.dwp.ms"})
public class DwpMsApiApplication {
    
    public static void main(String[] args) { // Main entry point for the application.
        SpringApplication.run(DwpMsApiApplication.class, args); // Starts the Spring application.
    }
}
