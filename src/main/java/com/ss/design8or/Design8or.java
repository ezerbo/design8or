package com.ss.design8or;

import com.ss.design8or.config.ServiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * @author ezerbo
 *
 */
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(value = { ServiceProperties.class })
public class Design8or {

	private static final Logger log = LoggerFactory.getLogger(Design8or.class);

	public static void main(String[] args) {
		SpringApplication design8or = new SpringApplication(Design8or.class);
		Environment env = design8or.run(args).getEnvironment();

		String appName = env.getProperty("spring.application.name", "Application");
		String port = env.getProperty("server.port", "8080");
		String contextPath = env.getProperty("server.servlet.context-path", "");

		log.info("""
                        
                        ----------------------------------------------------------
                            {} is running! Access URLs:
                            Local:      http://localhost:{}{}
                            External:   http://{}:{}{}
                            Profile(s): {}
                        ----------------------------------------------------------""",
				appName,
				port, contextPath,
				getHostAddress(), port, contextPath,
				Arrays.toString(env.getActiveProfiles()));
	}

	private static String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

}