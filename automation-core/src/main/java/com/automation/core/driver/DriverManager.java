package com.automation.core.driver;

import io.appium.java_client.android.AndroidDriver;

/**
 * Interface for managing mobile driver.
 * This interface defines the contract for driver management operations,
 * allowing different implementations for different platforms/configurations.
 * @author Rom
 * @version 1.0
 */
public interface DriverManager {

    /**
     * Initializes and starts the mobile driver session.
     *
     * @throws com.automation.exceptions.DriverInitializationException if driver cannot be started
     */
    void startDriver();

    /**
     * Stops and cleans up the mobile driver session.
     */
    void stopDriver();

    /**
     * Gets the current AndroidDriver instance.
     *
     * @return The AndroidDriver instance, or null if not started
     */
    AndroidDriver getDriver();

    /**
     * Checks if the driver is currently active.
     *
     * @return true if driver is active, false otherwise
     */
    boolean isDriverActive();
}
