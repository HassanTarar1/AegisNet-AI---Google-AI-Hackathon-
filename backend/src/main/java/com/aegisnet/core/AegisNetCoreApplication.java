package com.aegisnet.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AegisNetCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(AegisNetCoreApplication.class, args);
	}

}
