package com.auctionaa.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
@EnableConfigurationProperties
public class AuctionBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionBackendApplication.class, args);
		System.out.println("Hello");
	}
}
