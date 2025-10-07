package com.automation.core.config;

import com.automation.exceptions.ConfigurationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON-based implementation of ConfigReader interface.
 *
 * This class reads configuration from a JSON file located in the classpath.
 * It uses Jackson ObjectMapper for JSON parsing and provides thread-safe
 * singleton access to configuration data.
 *
 * Demonstrates OOP principles:
 * - Implements ConfigReader interface (polymorphism)
 * - Singleton pattern for shared configuration
 * - Encapsulation of JSON parsing logic
 *
 * @author Automation Team
 * @version 1.0
 */
public class JsonConfigReader implements ConfigReader {

    private static final Logger log = LogManager.getLogger(JsonConfigReader.class);
    private static final String DEFAULT_CONFIG_FILE = "config/config.json";

    private final Map<String, Object> configData;

    /**
     * Constructs a JsonConfigReader using the default config file.
     *
     * @throws ConfigurationException if config file cannot be loaded
     */
    public JsonConfigReader() {
        this(DEFAULT_CONFIG_FILE);
    }

    /**
     * Constructs a JsonConfigReader using a specified config file.
     *
     * @param configFilePath Path to the config file in classpath
     * @throws ConfigurationException if config file cannot be loaded
     */
    @SuppressWarnings("unchecked")
    public JsonConfigReader(String configFilePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFilePath)) {
            if (inputStream == null) {
                throw new ConfigurationException(
                        "Configuration file not found: " + configFilePath);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            this.configData = objectMapper.readValue(inputStream, Map.class);

            log.info("Configuration loaded successfully from: {}", configFilePath);
            log.debug("Configuration keys: {}", configData.keySet());

        } catch (Exception e) {
            throw new ConfigurationException(
                    "Failed to load configuration from: " + configFilePath, e);
        }
    }

    @Override
    public Map<String, Object> getConfiguration() {
        // Return a copy to prevent external modification
        return new HashMap<>(configData);
    }

    @Override
    public String getString(String key) {
        return getString(key, null);
    }

    @Override
    public String getString(String key, String defaultValue) {
        Object value = configData.get(key);
        if (value == null) {
            log.debug("Config key '{}' not found, using default: {}", key, defaultValue);
            return defaultValue;
        }
        return String.valueOf(value);
    }

    @Override
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    @Override
    public int getInt(String key) {
        return getInt(key, 0);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid integer value for key '{}': {}. Using default: {}",
                    key, value, defaultValue);
            return defaultValue;
        }
    }
}
