package com.ss.design8or.error.exception;

/**
 * @author ezerbo
 *
 */
public class SubscriptionException extends RuntimeException {

	private static final long serialVersionUID = -5508114927757126772L;
	
	public SubscriptionException() {
		super("Subscription already exists");
	}

}