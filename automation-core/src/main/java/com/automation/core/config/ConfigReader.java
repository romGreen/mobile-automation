package com.automation.core.config;

import java.util.Map;

/**
 * Interface for reading configuration properties from various sources.
 *
 * This interface demonstrates the OOP principle of abstraction by defining
 * a contract for configuration reading without exposing implementation details.
 *
 * @author Rom
 * @version 1.0
 */
public interface ConfigReader {

    /**
     * Loads and returns the entire configuration as a map.
     *
     * @return Map containing all configuration key-value pairs
     * @throws com.automation.exceptions.ConfigurationException if loading fails
     */
    Map<String, Object> getConfiguration();

    /**
     * Retrieves a configuration value as a String.
     *
     * @param key The configuration key
     * @return The value as a String, or null if not found
     */
    String getString(String key);

    /**
     * Retrieves a configuration value as a String with a default value.
     *
     * @param key The configuration key
     * @param defaultValue The default value if key is not found
     * @return The value as a String, or defaultValue if not found
     */
    String getString(String key, String defaultValue);

    /**
     * Retrieves a configuration value as a boolean with a default value.
     *
     * @param key The configuration key
     * @param defaultValue The default value if key is not found
     * @return The value as a boolean, or defaultValue if not found
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * Retrieves a configuration value as an integer with a default value.
     *
     * @param key The configuration key
     * @param defaultValue The default value if key is not found
     * @return The value as an integer, or defaultValue if not found
     */
    int getInt(String key, int defaultValue);
}
