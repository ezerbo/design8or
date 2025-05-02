package com.ss.design8or.config.properties;

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
	
	private MailProperties mail = new MailProperties();
	
	private KeysProperties keys = new KeysProperties();

	private RotationProperties rotation = new RotationProperties();
	
	private CorsConfiguration cors = new CorsConfiguration();
}