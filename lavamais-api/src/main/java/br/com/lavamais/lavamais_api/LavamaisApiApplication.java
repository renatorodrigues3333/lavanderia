package br.com.lavamais.lavamais_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LavamaisApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LavamaisApiApplication.class, args);
	}

}
