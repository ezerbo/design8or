package com.ss.design8or.error.exception;

/**
 * @author ezerbo
 *
 */
public class DesignationNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 5538670063800598838L;
	
	public DesignationNotFoundException() {
		super("No designation Found. It may have already been accepted.");
	}

}