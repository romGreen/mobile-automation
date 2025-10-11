package com.automation.exceptions;

/**
 * Base exception class for all custom automation framework exceptions.
 * @author Rom
 * @version 1.0
 */
public class AutomationException extends RuntimeException {

    /**
     * Constructs a new AutomationException with the specified detail message.
     *
     * @param message The detail message explaining the exception
     */
    public AutomationException(String message) {
        super(message);
    }

    /**
     * Constructs a new AutomationException with the specified detail message and cause.
     *
     * @param message The detail message explaining the exception
     * @param cause   The underlying cause of this exception
     */
    public AutomationException(String message, Throwable cause) {
        super(message, cause);
    }
}
