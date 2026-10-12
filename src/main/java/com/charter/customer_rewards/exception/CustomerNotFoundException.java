package com.charter.customer_rewards.exception;

/**
 * Indicates that a requested customer could not be found.
 */
public class CustomerNotFoundException extends RuntimeException {

    /**
     * Creates the exception with a descriptive message.
     *
     * @param message explanation of the missing customer
     */
    public CustomerNotFoundException(String message) {
        super(message);
    }
}