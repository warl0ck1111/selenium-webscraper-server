package com.example.seleniumdemo;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;


@Slf4j
@Async
@Configuration
@SpringBootApplication
public class SeleniumDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeleniumDemoApplication.class, args);

	}

}
