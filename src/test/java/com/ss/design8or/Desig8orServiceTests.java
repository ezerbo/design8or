package com.ss.design8or;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;

/**
 * @author ezerbo
 *
 */
public class Desig8orServiceTests {
	
	

	@Test
	public void testDesignate() {
		BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
		basicTextEncryptor.setPasswordCharArray("test-key".toCharArray());
		System.out.println(basicTextEncryptor.encrypt("test@test.com"));
	}
}
