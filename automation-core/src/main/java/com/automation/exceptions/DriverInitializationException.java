package com.automation.exceptions;

/**
 * Exception thrown when driver initialization or startup fails.
 *
 * This exception is thrown when the mobile driver (Appium) cannot be
 * initialized, typically due to:
 * - Appium server not running
 * - Invalid capabilities
 * - Device/emulator not available
 * - Network connection issues
 *
 * @author Automation Team
 * @version 1.0
 */
public class DriverInitializationException extends AutomationException {

    /**
     * Constructs a DriverInitializationException with a detailed message.
     *
     * @param message The detail message
     */
    public DriverInitializationException(String message) {
        super(message);
    }

    /**
     * Constructs a DriverInitializationException with message and cause.
     *
     * @param message The detail message
     * @param cause   The underlying cause
     */
    public DriverInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
