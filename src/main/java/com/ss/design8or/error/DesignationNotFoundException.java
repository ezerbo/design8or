package com.ss.design8or.error;

/**
 * @author ezerbo
 *
 */
public class DesignationNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 5538670063800598838L;
	
	public DesignationNotFoundException(String message) {
		super(message);
	}
	
	public DesignationNotFoundException() {
		super();
	}

}