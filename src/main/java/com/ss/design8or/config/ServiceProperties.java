package com.ss.design8or.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import com.ss.design8or.model.KeysConfig;
import com.ss.design8or.model.MailConfig;

import lombok.Data;

/**
 * @author ezerbo
 *
 */
@Data
@ConfigurationProperties(prefix = "app")
public class ServiceProperties {
	
	private MailConfig mail = new MailConfig();
	
	private KeysConfig keys = new KeysConfig();
	
	private CorsConfiguration cors = new CorsConfiguration();
}