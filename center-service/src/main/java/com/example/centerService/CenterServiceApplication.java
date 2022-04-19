package com.example.centerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example")
public class CenterServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CenterServiceApplication.class, args);
	}

}
