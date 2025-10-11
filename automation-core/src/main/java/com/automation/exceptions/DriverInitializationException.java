package com.automation.exceptions;

/**
 * Exception thrown when driver initialization or startup fails.
 * This exception is thrown when the mobile driver (Appium) cannot be
 * initialized because:
 * - Appium server not running
 * - Invalid capabilities
 * - Device/emulator not available
 * - Network connection issues
 *
 * @author Rom
 * @version 1.0
 */
public class DriverInitializationException extends AutomationException {
    /**
     * DriverInitializationException with message and cause.
     *
     * @param message The detail message
     * @param cause   The underlying cause
     */
    public DriverInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
