package com.flowtech.eventhub_spring_boot_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@OpenAPIDefinition(
	    info = @Info(
	        title = "Event-Hub-An-Event-Booking-Management-Application",
	        version = "1.0",
	        description = "Spring Boot REST API with JWT authentication",
	        contact = @Contact(
	        		email = "anans@gmail.com",
	        		url = "www.url.com"
	        )
	    )
	   
	)

@SecurityScheme(
	    name = "bearerAuth",
	    type = SecuritySchemeType.HTTP,
	    scheme = "bearer",
	    bearerFormat = "JWT",
	    in = SecuritySchemeIn.HEADER
	)
public class EventhubSpringBootBackendApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext=SpringApplication.run(EventhubSpringBootBackendApplication.class, args);
	
		ConfigurableEnvironment environment=applicationContext.getEnvironment();
		
		System.out.println("Event-Hub is running on port = "+environment.getProperty("server.port"));
	}

}
