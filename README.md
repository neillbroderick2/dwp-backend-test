# DWP User by Radius API Microservice

This is a test microservice which enables users to make API calls to obtain users by radius. It has been built using the Spring Boot Java framework. 

The default API call in this application contacts external API's to obtain users which are within 50 miles of London. An additional call has been created that will allow for any city name and radius to be passed as path parameters to add flexibility to users.

## Features

### Modular Design

The application is split into three modules:

- ```gov.dwp.ms.api```
- ```gov.dwp.ms.model```
- ```gov.dwp.ms.service```

Each of these modules are designed to be reused. By storing the individual packages on a repository such as Artifactory / Nexus they will be available for future projects that require the same models / service calls. Reusing modules in this way can save development time in the future when developing additional microservices.

### Swagger Documentation

[Swagger](https://mvnrepository.com/artifact/io.springfox/springfox-swagger-ui/2.10.5) documentation has been included to make testing the API easier. Adding this requires minimal effort in development as the Swagger module can automatically find and describe rest API calls on controllers. This is extremely useful for developers looking to learn the API as it lays out all the information on a web page, with the ability to test.

### Concurrency

The application makes use of concurrency when making external API calls by using Java ```CompletableFutures``` objects. This speeds up the response as multiple calls are made at once so the application is only ever waiting for the slowest call to complete.

### Actuator

[Spring Actuator](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator/2.3.1.RELEASE) has been included as this provides endpoints for health check statuses. This is useful in a production environment where monitoring software can check to see if the service is still active and take remedial action if necessary.

### Validation

Path variables are validated using [Spring Boot Validation](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-validation/2.3.1.RELEASE). This enables the application to check and reject any out of bounds path variables. For this application both the ```city``` and ```radius``` path variables are validated.

### Cache

For results from the Location API call a cache has been implemented using [Spring Cache](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-cache/2.3.1.RELEASE). As the results are just co-ordinates of cities it is safe to cache them as they are never going to change. Caching results from the Users API would be more difficult as the details *may* have changed since the cache entry was created. 

### Retries

A retry mechanism has been included that will trigger on server errors (5xx) for external API calls. This uses [Spring Retry](https://mvnrepository.com/artifact/org.springframework.retry/spring-retry/1.3.0) to re-invoke the failed method when a server error occurs. This could be extended to capture other types of errors and also throw back different responses based on the error type. Currently it is set to retry up to 3 times with a 500ms interval between tries.

### Unit Testing

The application contains unit tests for both the ```api``` and ```service``` modules. The tests have been written using [Spring Boot Test](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test/2.3.1.RELEASE) and [JUnit](https://mvnrepository.com/artifact/junit/junit/4.13).

### Inverse/Forward Radius Formula
To ensure the most accurate calculation of radius, the inverse/forward formula was chosen over others such as haversine. This method is more accurate as it takes into consideration that the Earth not spherically perfect, whereas others treat it as a true sphere. Over distance this discrepancy can build up and so to ensure the best possible results the inverse/forward method was chosen. A Java implementation of [GeographicLib](https://mvnrepository.com/artifact/net.sf.geographiclib/GeographicLib-Java/1.50) exists already so this was used in the application.

## Installation

### Prerequisites

This application requires compiling with the following prerequisites:

- [Java 13](https://openjdk.java.net/projects/jdk/13/)
- [Maven 3.5+](https://archive.apache.org/dist/maven/maven-3/3.5.0/)

Once you have both prerequisites installed you can build the application using the ```mvn clean package``` command from the root project folder directory.

### Profiles

There are several profiles in the application to mimic the different environments. By default the ```test``` profile will load when running unit tests and the ```dev``` profile will run when running the application. To run a different profile (e.g. production) you can compile the program using ```mvn clean package -Pprod```.

### Running Application

The application can be run in a console window using the command:

```java --enable-preview -jar .\dwp-backend-test-api\target\dwp-backend-test-api-0.0.1-api-SNAPSHOT.jar```

It will take a few seconds for the application to start and once it does it will be ready to accept API requests.

### Testing Application

The application will be running on ```https://localhost:8443``` once is has started. There are several ways to test the application works such as console commands and Swagger UI.

#### Testing Via Console

To test via the console you will need a library such as ```cURL``` installed on your machine. An example of this command is as follows:

```curl -k https://localhost:8443/city/london/radius/50/users```

#### Testing Via Swagger

The application also provides a Swagger documentation that can be used to test API calls. To access the Swagger web page visit the following URL in a web browser when the application is running:

```https://localhost:8443/swagger-ui.html```
