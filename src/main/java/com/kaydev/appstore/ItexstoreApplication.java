package com.kaydev.appstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.kaydev.appstore.services.MigrationService;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class ItexstoreApplication {
	@Autowired
	MigrationService migrationService;

	public static void main(String[] args) {
		SpringApplication.run(ItexstoreApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			try {
				migrationService.migrate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

}
