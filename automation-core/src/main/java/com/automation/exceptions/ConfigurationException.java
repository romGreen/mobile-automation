package com.automation.exceptions;

/**
 * Exception thrown when configuration loading or parsing fails.
 *
 * This exception indicates problems with the framework configuration,
 * such as missing config files, invalid JSON, or missing required properties.
 *
 * @author Automation Team
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

    /**
     * Constructs a ConfigurationException for a specific configuration key.
     *
     * @param configKey The configuration key that caused the issue
     * @param message   Additional context message
     */
    public ConfigurationException(String configKey, String message) {
        super(String.format("Configuration error for key '%s': %s", configKey, message));
        this.configKey = configKey;
    }

    /**
     * Gets the configuration key that caused the exception.
     *
     * @return Configuration key, or null if not provided
     */
    public String getConfigKey() {
        return configKey;
    }
}
