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
	
	private DesignationEmailProperties designationEmail = new DesignationEmailProperties();
	
	private BrowserPushNotificationKeys browserPushNotificationKeys = new BrowserPushNotificationKeys();

	private RotationProperties rotation = new RotationProperties();
	
	private CorsConfiguration cors = new CorsConfiguration();
}