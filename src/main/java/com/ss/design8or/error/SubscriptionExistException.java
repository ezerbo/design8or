package com.ss.design8or.error;

/**
 * @author ezerbo
 *
 */
public class SubscriptionExistException extends RuntimeException {

	private static final long serialVersionUID = -5508114927757126772L;
	
	public SubscriptionExistException() {
		super("Subscription already exists");
	}

}