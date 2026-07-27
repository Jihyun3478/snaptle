package com.snaptle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SnaptleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnaptleApplication.class, args);
	}

}
