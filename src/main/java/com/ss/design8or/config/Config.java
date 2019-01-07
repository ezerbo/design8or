package com.ss.design8or.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @author ezerbo
 *
 */
@Configuration
public class Config extends WebSecurityConfigurerAdapter {
	
	private CorsConfiguration config;

	public Config(ServiceProperties properties) {
		this.config = properties.getCors();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.headers()
			.frameOptions().sameOrigin()
		.and()
			.cors()
		.and()
		.csrf()
		.disable();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		config.addAllowedHeader("*");
		config.addAllowedOrigin("*");
		config.setAllowCredentials(true);
		config.addAllowedMethod("OPTIONS");
		config.addAllowedMethod("GET");
		config.addAllowedMethod("POST");
		config.addAllowedMethod("PUT");
		config.addAllowedMethod("DELETE");
		config.addExposedHeader("X-Total-Count");
		source.registerCorsConfiguration("/**", config);
		return source;
	}
	
}
