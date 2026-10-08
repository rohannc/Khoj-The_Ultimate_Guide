package com.rohan.Khoj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class KhojApplication {

	public static void main(String[] args) {
		SpringApplication.run(KhojApplication.class, args);
	}

}
