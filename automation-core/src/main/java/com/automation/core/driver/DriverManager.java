package com.automation.core.driver;

import io.appium.java_client.android.AndroidDriver;

/**
 * Interface for managing mobile driver lifecycle.
 *
 * This interface defines the contract for driver management operations,
 * allowing different implementations for different platforms or configurations.
 *
 * Demonstrates OOP principles:
 * - Abstraction: Hides driver management complexity
 * - Interface Segregation: Focused, minimal interface
 * - Dependency Inversion: Tests depend on this interface, not concrete implementation
 *
 * @author Automation Team
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
