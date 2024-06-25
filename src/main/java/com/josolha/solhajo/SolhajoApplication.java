package com.josolha.solhajo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SolhajoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolhajoApplication.class, args);
	}

}
