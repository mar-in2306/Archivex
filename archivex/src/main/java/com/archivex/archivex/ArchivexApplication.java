package com.archivex.archivex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ArchivexApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArchivexApplication.class, args);
	}
}
