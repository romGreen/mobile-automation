package com.automation.core.driver;

import com.automation.core.config.ConfigReader;
import com.automation.exceptions.DriverInitializationException;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Duration;

/**
 * Appium-based implementation of DriverManager interface.
 *
 * This class manages the lifecycle of an Appium AndroidDriver instance.
 * It handles driver initialization, configuration, and cleanup.
 *
 * Demonstrates OOP principles:
 * - Implements DriverManager interface (polymorphism)
 * - Encapsulates driver creation logic
 * - Uses dependency injection for ConfigReader
 * - Thread-safe driver management
 *
 * @author Automation Team
 * @version 1.0
 */
@Component
public class AppiumDriverManager implements DriverManager {

    private static final Logger log = LogManager.getLogger(AppiumDriverManager.class);

    private AndroidDriver driver;
    private final ConfigReader configReader;

    /**
     * Constructs an AppiumDriverManager with injected configuration reader.
     *
     * @param configReader The configuration reader to use for driver setup
     */
    @Autowired
    public AppiumDriverManager(ConfigReader configReader) {
        this.configReader = configReader;
    }

    @Override
    public synchronized void startDriver() {
        if (driver != null) {
            log.warn("Driver already started, skipping initialization");
            return;
        }

        try {
            log.info("Starting Appium driver...");

            // Build driver options from configuration
            UiAutomator2Options options = buildDriverOptions();

            // Create driver instance
            String serverUrl = configReader.getString("appiumServerUrl", "http://127.0.0.1:4723");
            driver = new AndroidDriver(new URL(serverUrl), options);

            log.info("Driver started successfully");
            log.debug("Available contexts: {}", driver.getContextHandles());

            // Ensure app is in foreground
            ensureAppInForeground();

        } catch (Exception e) {
            driver = null;
            throw new DriverInitializationException(
                    "Failed to initialize Appium driver. " +
                    "Ensure Appium server is running and device/emulator is available.", e);
        }
    }

    @Override
    public synchronized void stopDriver() {
        if (driver == null) {
            log.debug("Driver already stopped or never started");
            return;
        }

        try {
            log.info("Stopping Appium driver...");
            driver.quit();
            log.info("Driver stopped successfully");
        } catch (Exception e) {
            log.error("Error while stopping driver", e);
        } finally {
            driver = null;
        }
    }

    @Override
    public AndroidDriver getDriver() {
        if (driver == null) {
            throw new IllegalStateException(
                    "Driver is not initialized. Call startDriver() first.");
        }
        return driver;
    }

    @Override
    public boolean isDriverActive() {
        return driver != null;
    }

    /**
     * Builds UiAutomator2Options from configuration.
     *
     * This method encapsulates the complexity of driver configuration,
     * making it easy to modify or extend capabilities.
     *
     * @return Configured UiAutomator2Options
     */
    private UiAutomator2Options buildDriverOptions() {
        UiAutomator2Options options = new UiAutomator2Options();

        // Basic capabilities
        options.setPlatformName(configReader.getString("platformName", "Android"));
        options.setAutomationName(configReader.getString("automationName", "UiAutomator2"));
        options.setDeviceName(configReader.getString("deviceName", "Android Device"));

        // Timeouts
        int commandTimeout = configReader.getInt("newCommandTimeout", 120);
        options.setNewCommandTimeout(Duration.ofSeconds(commandTimeout));

        // UDID (for specific device)
        String udid = configReader.getString("udid", "");
        if (!udid.isBlank()) {
            options.setUdid(udid);
            log.info("Using device with UDID: {}", udid);
        }

        // App package and activity (for installed app)
        String appPackage = configReader.getString("appPackage", "");
        String appActivity = configReader.getString("appActivity", "");

        if (!appPackage.isBlank() && !appActivity.isBlank()) {
            options.setAppPackage(appPackage);
            options.setAppActivity(appActivity);
            options.setAppWaitActivity(configReader.getString("appWaitActivity", "*"));

            // Don't reset app state between sessions
            if (configReader.getBoolean("noReset", true)) {
                options.setNoReset(true);
            }

            log.info("Launching installed app: {}/{}", appPackage, appActivity);
        } else {
            // Fallback: install from APK
            String appPath = configReader.getString("app", "");
            if (!appPath.isBlank()) {
                options.setApp(appPath);
                log.info("Installing app from: {}", appPath);
            }
        }

        // WebView support
        options.setCapability("ensureWebviewsHavePages",
                configReader.getBoolean("ensureWebviewsHavePages", false));
        options.setCapability("chromedriverAutodownload",
                configReader.getBoolean("chromedriverAutodownload", true));
        options.setCapability("webviewConnectTimeout",
                configReader.getInt("webviewConnectTimeout", 15000));

        // Security - allow adb_shell for advanced text input
        options.setCapability("allowInsecure", new String[]{"adb_shell"});

        // Auto-grant permissions
        if (configReader.getBoolean("autoGrantPermissions", true)) {
            options.setAutoGrantPermissions(true);
        }

        return options;
    }

    /**
     * Ensures the app is in the foreground after driver starts.
     *
     * This handles edge cases where the app might not be the active app
     * after session creation.
     */
    private void ensureAppInForeground() {
        String appPackage = configReader.getString("appPackage", "");
        if (appPackage.isBlank()) {
            return;
        }

        try {
            String currentPackage = driver.getCurrentPackage();
            if (currentPackage == null || !currentPackage.equals(appPackage)) {
                log.info("App not in foreground, activating: {}", appPackage);
                driver.activateApp(appPackage);
            }
        } catch (Exception e) {
            log.warn("Could not verify/activate app foreground status", e);
            // Try to activate anyway
            try {
                driver.activateApp(appPackage);
            } catch (Exception ignored) {
            }
        }
    }
}
