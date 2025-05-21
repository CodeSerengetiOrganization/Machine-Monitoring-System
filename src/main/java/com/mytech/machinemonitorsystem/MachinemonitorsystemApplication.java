package com.mytech.machinemonitorsystem;

//import com.mytech.machinemonitorsystem.config.WebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

//@SpringBootApplication(excludeName = {"org.springframework.boot.autoconfigure.web.servlet.error.ErrorPageFilterAutoConfiguration"})
//@ComponentScan("com.mytech.machinemonitorsystem")
@SpringBootApplication
//@ServletComponentScan // Add this annotation
public class MachinemonitorsystemApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(MachinemonitorsystemApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(MachinemonitorsystemApplication.class, args);
	}

}
