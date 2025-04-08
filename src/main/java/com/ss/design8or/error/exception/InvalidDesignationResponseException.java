package com.ss.design8or.error.exception;

/**
 * @author ezerbo
 *
 */
public class InvalidDesignationResponseException extends RuntimeException {

	private static final long serialVersionUID = 1792727167693325615L;
	
	public InvalidDesignationResponseException(String response) {
		super(String.format("No such designation response '%s'", response));
	}

}