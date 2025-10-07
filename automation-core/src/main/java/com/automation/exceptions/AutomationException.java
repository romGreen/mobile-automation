package com.automation.exceptions;

/**
 * Base exception class for all custom automation framework exceptions.
 *
 * This class serves as the parent for all framework-specific exceptions,
 * allowing unified exception handling and providing a clear distinction
 * between framework exceptions and standard Java exceptions.
 *
 * Demonstrates OOP principle of inheritance and exception hierarchy.
 *
 * @author Automation Team
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

    /**
     * Constructs a new AutomationException with the specified cause.
     *
     * @param cause The underlying cause of this exception
     */
    public AutomationException(Throwable cause) {
        super(cause);
    }
}
