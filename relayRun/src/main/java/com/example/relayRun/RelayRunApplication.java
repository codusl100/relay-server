package com.example.relayRun;

import com.google.firebase.FirebaseApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RelayRunApplication {

	public static void main(String[] args) {
		SpringApplication.run(RelayRunApplication.class, args);
	}
}
