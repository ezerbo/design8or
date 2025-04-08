package com.ss.design8or.error.exception;

/**
 * @author ezerbo
 *
 */
public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4379359382365251375L;
	
	public UserNotFoundException(Long userId) {
		super(String.format("No user found with identifier: '%s'", userId));
	}
	
	public UserNotFoundException(String emailAddress) {
		super(String.format("No user found with email address: '%s'", emailAddress));
	}

}
