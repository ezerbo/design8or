package com.ss.design8or.error;

/**
 * @author ezerbo
 *
 */
public class DesignationNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 5538670063800598838L;
	
//	public DesignationNotFoundException(String emailAddress) {
//		super(String.format("No designation found for '%s'", emailAddress));
//	}
//	
	public DesignationNotFoundException() {
		super("No designation Found. I might have already been accepted.");
	}

}