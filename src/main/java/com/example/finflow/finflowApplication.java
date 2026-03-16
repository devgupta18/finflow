package com.example.finflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class finflowApplication {
	public static void main(String[] args) {
        System.out.println("Application Started");
		SpringApplication.run(finflowApplication.class, args);
        System.out.println("Application Ended");
	}
}
