package com.automation.exceptions;

/**
 * Exception thrown when configuration loading or parsing fails.
 *
 * This exception indicates problems with the framework configuration,
 * such as missing config files, invalid JSON or missing required properties.
 *
 * @author Rom
 * @version 1.0
 */
public class ConfigurationException extends AutomationException {

    private final String configKey;

    /**
     * Constructs a ConfigurationException with a detailed message.
     *
     * @param message The detail message
     */
    public ConfigurationException(String message) {
        super(message);
        this.configKey = null;
    }

    /**
     * Constructs a ConfigurationException with message and cause.
     *
     * @param message The detail message
     * @param cause   The underlying cause
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
        this.configKey = null;
    }
}
