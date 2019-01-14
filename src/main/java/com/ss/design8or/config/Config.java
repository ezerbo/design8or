package com.ss.design8or.config;

import java.util.concurrent.Executor;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ss.design8or.model.MailConfig;

/**
 * @author ezerbo
 *
 */
@Configuration
public class Config extends WebSecurityConfigurerAdapter {
	
	private MailConfig mailConfig;

	private CorsConfiguration config;

	public Config(ServiceProperties properties) {
		this.config = properties.getCors();
		this.mailConfig = properties.getMail();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable()
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
	
	@Bean
	public BasicTextEncryptor basicTextEncryptor() {
		BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
		basicTextEncryptor.setPasswordCharArray(mailConfig.getEmailEncryptionKey().toCharArray());
    	return basicTextEncryptor;
	}
	
	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(30);
		executor.setThreadNamePrefix("Notification-");
		executor.initialize();
		return executor;
	}
	
}