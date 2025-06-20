package com.mytech.machinemonitorsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync //Crucial for non-blocking email sends
@EnableScheduling
public class MachinemonitorsystemApplication extends SpringBootServletInitializer {
//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//		return application.sources(MachinemonitorsystemApplication.class);
//	}

	public static void main(String[] args) {
		SpringApplication.run(MachinemonitorsystemApplication.class, args);
	}

}
