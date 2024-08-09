package com.santi.messagesp2p;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
public class Messagesp2pApplication {

	public static void main(String[] args) {
		SpringApplication.run(Messagesp2pApplication.class, args);
	}

}
