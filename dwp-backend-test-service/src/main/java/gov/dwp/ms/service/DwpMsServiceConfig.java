package gov.dwp.ms.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;


@EnableAsync
@EnableCaching
@EnableRetry
@Configuration
public class DwpMsServiceConfig {
    
    @Value("${thread.core.pool.size}")
    private int corePoolSize;
	
    @Value("${thread.max.pool.size}")
    private int maxPoolSize;
	
	@Value("${thread.queue.capacity}")
    private int queueCapacity;
	
	@Bean
    public ThreadPoolTaskExecutor dwpTaskExec() { // Bean to create a thread pool that is used by the future methods in the service.
		// There are 3 futures that may need to run concurrently in this service.
		// Thread pool is setup to use core pool size of 3 times the Tomcat 200 connection default limit.
		// This will enable the program to always have 3 threads available for each potential Tomcat connection.
		// Max pool size is set also to 600 to create a fixed size thread pool based on available connections.
		// Queue capacity is set to 300, which means we could potentially queue another 100 requests whilst waiting for busy threads to become available (Tomcat limit may need increasing). 
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    	executor.setCorePoolSize(corePoolSize);
    	executor.setMaxPoolSize(maxPoolSize);
    	executor.setQueueCapacity(queueCapacity);
    	executor.setThreadNamePrefix("dwp-ms-exec-");
    	executor.initialize();
        
        return executor;
    }
	
	@Bean
    public RestTemplate restTemplate() throws Exception { // Bean to setup a basic rest template to make api calls.
        return new RestTemplate();
    }
}