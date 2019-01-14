package com.ss.design8or;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author ezerbo
 *
 */
@TestConfiguration
public class TestConfig {

	@Bean
	public BasicTextEncryptor basicTextEncryptor() {
		BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
		basicTextEncryptor.setPasswordCharArray("test-key".toCharArray());
    	return basicTextEncryptor;
	}
}
