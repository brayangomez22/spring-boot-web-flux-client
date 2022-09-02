package com.springbootwebfluxclientapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringBootWebFluxClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebFluxClientApplication.class, args);
	}

}
