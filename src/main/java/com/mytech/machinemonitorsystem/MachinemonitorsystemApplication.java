package com.mytech.machinemonitorsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@SpringBootApplication
@ComponentScan("com.mytech.machinemonitorsystem")
public class MachinemonitorsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MachinemonitorsystemApplication.class, args);
	}

}
