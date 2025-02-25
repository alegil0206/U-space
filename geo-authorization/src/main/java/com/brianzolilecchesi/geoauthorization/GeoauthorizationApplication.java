package com.brianzolilecchesi.geoauthorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GeoauthorizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeoauthorizationApplication.class, args);
	}

}
