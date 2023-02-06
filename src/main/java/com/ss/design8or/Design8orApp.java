package com.ss.design8or;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ss.design8or.config.ServiceProperties;

/**
 * @author ezerbo
 *
 */
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(value = { ServiceProperties.class })
public class Design8orApp extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(Design8orApp.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(Design8orApp.class);
	}
}