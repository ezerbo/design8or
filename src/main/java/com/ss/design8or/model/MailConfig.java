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
public class MailConfig {

	private String from;
	
	private String designationEmailSubject;
	
	private String designationResponseBaseUrl;
	
	private String emailEncryptionKey;
	
}