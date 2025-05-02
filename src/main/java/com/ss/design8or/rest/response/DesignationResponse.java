package com.ss.design8or.rest.response;

import com.ss.design8or.model.DesignationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ezerbo
 *
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DesignationResponse {
	
	private long designationId;

	private Date designationDate;

	private DesignationStatus status;

	private DesignationStatus previousStatus;

	private String emailAddress;

	private String message;
}
