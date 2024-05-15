package com.calendar.restapicalendar;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.calendar")
public class RestApiCalendarApplication {
	public static void main(String[] args) {
		SpringApplication.run(RestApiCalendarApplication.class, args);
	}
}
