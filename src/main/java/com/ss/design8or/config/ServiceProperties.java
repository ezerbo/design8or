package com.ss.design8or.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import lombok.Data;

/**
 * @author ezerbo
 *
 */
@Data
@ConfigurationProperties(prefix = "app")
public class ServiceProperties {
	
	private CorsConfiguration cors = new CorsConfiguration();
	
}
