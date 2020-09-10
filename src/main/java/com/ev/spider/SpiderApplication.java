package com.ev.spider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpiderApplication {
	public static String[] args;
	public static ConfigurableApplicationContext context;

	public static void main(String[] args) {

		SpringApplication.run(SpiderApplication.class, args);
	}

}
