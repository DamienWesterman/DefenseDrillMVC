package com.damienwesterman.defensedrill.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DefenseDrillMvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(DefenseDrillMvcApplication.class, args);
	}

}
