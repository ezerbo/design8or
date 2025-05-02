package com.ss.design8or.config.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ezerbo
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeysProperties {

	private String publicKey;

	private String privateKey;
	
	private String subject;
	
}
