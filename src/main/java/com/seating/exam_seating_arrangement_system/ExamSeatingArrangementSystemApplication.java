package com.seating.exam_seating_arrangement_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExamSeatingArrangementSystemApplication {

private static  final Logger logger = LoggerFactory.getLogger(ExamSeatingArrangementSystemApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(ExamSeatingArrangementSystemApplication.class, args);
		logger.info("Tomcat started on port 8080 (http) with context path '/' ");
	}

}
