package com.ss.design8or.config;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.concurrent.Executor;

import com.ss.design8or.config.properties.BrowserPushNotificationKeys;
import com.ss.design8or.config.properties.ServiceProperties;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authz) -> authz
						.anyRequest().permitAll())
				.cors(Customizer.withDefaults()).csrf().disable();
		return http.build();
	}

	@Bean
	public PushService pushService(ServiceProperties properties) throws GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());
		BrowserPushNotificationKeys keys = properties.getBrowserPushNotificationKeys();
		return new PushService(keys.getPublicKey(), keys.getPrivateKey(), keys.getSubject());
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource(ServiceProperties properties) {
		CorsConfiguration corsConfiguration = properties.getCors();
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.setAllowCredentials(false);
	//	config.setAllowCredentials(true);
		corsConfiguration.addAllowedMethod("OPTIONS");
		corsConfiguration.addAllowedMethod("GET");
		corsConfiguration.addAllowedMethod("POST");
		corsConfiguration.addAllowedMethod("PUT");
		corsConfiguration.addAllowedMethod("DELETE");
		corsConfiguration.addExposedHeader("X-Total-Count");
		source.registerCorsConfiguration("/**", corsConfiguration);
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