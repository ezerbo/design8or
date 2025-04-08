package com.ss.design8or.error.exception;

/**
 * @author ezerbo
 *
 */
public class EmailAddressInUseException extends RuntimeException {

	public EmailAddressInUseException(String emailAddress) {
		super(String.format("Email address '%s' is already in use", emailAddress));
	}

}
