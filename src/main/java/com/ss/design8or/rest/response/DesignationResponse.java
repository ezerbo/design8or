package com.ss.design8or.rest.response;

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
public class DesignationResponse {
	
	private String token;
	
	private String response; //accept or decline
	
	private String emailAddress;
}
