package com.ss.design8or.model;

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
public class KeysConfig {

	private String publicKey;

	private String privateKey;
	
	private String subject;
	
}
