package com.ss.design8or.error.exception;

/**
 * @author ezerbo
 *
 * Thrown when a designation cannot be accepted or created because the pool already has an accepted lead
 */
public class DesignationAlreadyClaimedException extends RuntimeException {

    public DesignationAlreadyClaimedException(String message) {
        super(message);
    }

    public DesignationAlreadyClaimedException(String message, Throwable cause) {
        super(message, cause);
    }
}
