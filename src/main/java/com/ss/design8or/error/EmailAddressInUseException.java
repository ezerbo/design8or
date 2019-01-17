package com.ss.design8or.error;

/**
 * @author ezerbo
 *
 */
public class EmailAddressInUseException extends RuntimeException {

	private static final long serialVersionUID = 6120250385063798775L;
	
	public EmailAddressInUseException(String emailAddress) {
		super(String.format("Email address '%s' is already in use", emailAddress));
	}

}
