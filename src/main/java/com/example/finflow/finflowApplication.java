package com.example.finflow;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class finflowApplication {
	public static void main(String[] args) {
        System.out.println("Application Started");
		SpringApplication.run(finflowApplication.class, args);
        System.out.println("Application Ended");
	}
}
