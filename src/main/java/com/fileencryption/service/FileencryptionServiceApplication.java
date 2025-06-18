package com.fileencryption.service;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
@ComponentScan(basePackages = {"com.fileencryption.service", "com.fileencryption.service.util"})

@SpringBootApplication
@EnableConfigurationProperties
public class FileencryptionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileencryptionServiceApplication.class, args);
	}

}
