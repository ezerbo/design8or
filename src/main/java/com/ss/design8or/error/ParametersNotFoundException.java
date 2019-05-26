package com.ss.design8or.error;

/**
 * Thrown when parameters are missing in the database.
 * 
 * @author ezerbo
 *
 */
public class ParametersNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2884139811517779334L;
	
	public ParametersNotFoundException() {
		super("No parameters found.");
	}

}
