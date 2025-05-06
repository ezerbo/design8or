package com.ss.design8or.config;

import java.util.concurrent.Executor;

import com.ss.design8or.config.properties.ServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @author ezerbo
 *
 */
@Configuration
public class ContextConfig {
	
	private final CorsConfiguration config;

	public ContextConfig(ServiceProperties properties) {
		this.config = properties.getCors();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authz) -> authz
						.anyRequest().permitAll())
				.cors(Customizer.withDefaults()).csrf().disable();
		return http.build();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		config.addAllowedHeader("*");
		config.addAllowedOrigin("*");
		config.setAllowCredentials(false);
	//	config.setAllowCredentials(true);
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
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(30);
		executor.setThreadNamePrefix("Comm-");
		executor.initialize();
		return executor;
	}
	
}