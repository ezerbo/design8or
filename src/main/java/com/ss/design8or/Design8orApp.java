package com.ss.design8or;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ss.design8or.config.ServiceProperties;

/**
 * @author ezerbo
 *
 */
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(value = { ServiceProperties.class })
public class Design8orApp {

	public static void main(String[] args) {
		SpringApplication.run(Design8orApp.class, args);
	}

}