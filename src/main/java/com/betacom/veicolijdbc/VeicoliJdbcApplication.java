package com.betacom.veicolijdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.betacom.veicolijdbc.components.QueryComponent;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@SpringBootApplication
public class VeicoliJdbcApplication {
	
	@Autowired
	private QueryComponent queries;

	public static void main(String[] args) {
		SpringApplication.run(VeicoliJdbcApplication.class, args);
	}
	
	@Bean
	CommandLineRunner startup() {
		return args -> {
			log.debug("Start jdbc managed by SpringBoot ***");
			queries.execute();
		};
	}

}
