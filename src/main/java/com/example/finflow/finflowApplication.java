package com.example.finflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class finflowApplication {

	public static void main(String[] args) {
        System.out.println("Application Started");
		SpringApplication.run(finflowApplication.class, args);
        System.out.println("Application Ended");
	}

}
