package com.argha.telestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class TelestoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelestoreApplication.class, args);
	}

}
