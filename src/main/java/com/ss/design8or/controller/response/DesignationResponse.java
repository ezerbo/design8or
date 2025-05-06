package com.ss.design8or.controller.response;

import com.ss.design8or.model.enums.DesignationStatus;
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
	
	private long id;

	private Date designationDate;

	private DesignationStatus status;

	private String emailAddress;

	private String message;
}
