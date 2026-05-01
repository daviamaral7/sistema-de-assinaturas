package com.davi.sistema_de_assinaturas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SistemaDeAssinaturasApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaDeAssinaturasApplication.class, args);
	}

}
