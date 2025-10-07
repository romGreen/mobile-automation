package com.automation.core.context;

import com.automation.exceptions.ContextSwitchException;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Manager for switching between mobile app contexts (Native and WebView).
 *
 * In hybrid mobile apps, the UI can contain both native Android elements
 * and web elements rendered in WebViews. This class provides utilities
 * for seamlessly switching between these contexts.
 *
 * Demonstrates OOP principles:
 * - Single Responsibility: Handles only context switching
 * - Encapsulation: Hides context switching complexity
 * - Error handling: Uses custom exceptions for clarity
 *
 * @author Automation Team
 * @version 1.0
 */
@Component
@Scope("prototype")
public class MobileContextManager {

    private static final Logger log = LogManager.getLogger(MobileContextManager.class);

    private static final String NATIVE_CONTEXT = "NATIVE_APP";
    private static final String WEBVIEW_PREFIX = "WEBVIEW";
    private static final int DEFAULT_TIMEOUT_MS = 15000;
    private static final int POLL_INTERVAL_MS = 250;

    /**
     * Switches to the native app context.
     *
     * @param driver The AndroidDriver instance
     */
    public void switchToNative(AndroidDriver driver) {
        try {
            if (!NATIVE_CONTEXT.equals(driver.getContext())) {
                driver.context(NATIVE_CONTEXT);
                log.info("Switched to NATIVE_APP context");
            } else {
                log.debug("Already in NATIVE_APP context");
            }
        } catch (Exception e) {
            log.warn("Failed to switch to native context", e);
        }
    }

    /**
     * Switches to the WebView context for the specified app package.
     *
     * @param driver     The AndroidDriver instance
     * @param appPackage The app package to find WebView for (e.g., "com.atidcollege.bugtracker")
     * @return The WebView context name that was switched to
     * @throws ContextSwitchException if WebView context is not found
     */
    public String switchToWebView(AndroidDriver driver, String appPackage) {
        return switchToWebView(driver, appPackage, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Switches to the WebView context for the specified app package with timeout.
     *
     * This method polls for the WebView context within the timeout period,
     * which is useful when WebView might take time to initialize.
     *
     * @param driver     The AndroidDriver instance
     * @param appPackage The app package to find WebView for
     * @param timeoutMs  Maximum time to wait for WebView context (milliseconds)
     * @return The WebView context name that was switched to
     * @throws ContextSwitchException if WebView context is not found within timeout
     */
    public String switchToWebView(AndroidDriver driver, String appPackage, long timeoutMs) {
        long endTime = System.currentTimeMillis() + timeoutMs;
        Set<String> availableContexts = null;
        int attempts = 0;

        while (System.currentTimeMillis() < endTime) {
            attempts++;
            availableContexts = driver.getContextHandles();

            // Log attempts periodically
            if (attempts % 8 == 0) {
                log.debug("Waiting for WebView context... Attempt: {}, Available: {}",
                        attempts, availableContexts);
            }

            // Search for WebView context matching the app package
            for (String context : availableContexts) {
                if (context != null && context.startsWith(WEBVIEW_PREFIX) && context.contains(appPackage)) {
                    driver.context(context);
                    log.info("Switched to WebView context: {}", context);
                    return context;
                }
            }

            sleep(POLL_INTERVAL_MS);
        }

        // Timeout reached - throw detailed exception
        throw new ContextSwitchException(
                WEBVIEW_PREFIX + "_" + appPackage,
                availableContexts != null ? availableContexts.toString() : "unknown",
                String.format("WebView context not found after %d ms. " +
                        "Ensure the app's WebView is enabled for debugging and the screen is loaded.",
                        timeoutMs)
        );
    }

    /**
     * Attempts to switch to WebView context without throwing exceptions.
     *
     * This is a non-throwing variant useful for conditional WebView handling.
     *
     * @param driver     The AndroidDriver instance
     * @param appPackage The app package to find WebView for
     * @param timeoutMs  Maximum time to wait for WebView context
     * @return true if successfully switched to WebView, false otherwise
     */
    public boolean trySwitchToWebView(AndroidDriver driver, String appPackage, long timeoutMs) {
        try {
            switchToWebView(driver, appPackage, timeoutMs);
            return true;
        } catch (ContextSwitchException e) {
            log.debug("WebView not available: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets all available contexts.
     *
     * @param driver The AndroidDriver instance
     * @return Set of available context names
     */
    public Set<String> getAvailableContexts(AndroidDriver driver) {
        return driver.getContextHandles();
    }

    /**
     * Gets the current active context.
     *
     * @param driver The AndroidDriver instance
     * @return The current context name
     */
    public String getCurrentContext(AndroidDriver driver) {
        return driver.getContext();
    }

    /**
     * Checks if currently in native context.
     *
     * @param driver The AndroidDriver instance
     * @return true if in native context, false otherwise
     */
    public boolean isNativeContext(AndroidDriver driver) {
        return NATIVE_CONTEXT.equals(driver.getContext());
    }

    /**
     * Checks if currently in a WebView context.
     *
     * @param driver The AndroidDriver instance
     * @return true if in WebView context, false otherwise
     */
    public boolean isWebViewContext(AndroidDriver driver) {
        String context = driver.getContext();
        return context != null && context.startsWith(WEBVIEW_PREFIX);
    }

    /**
     * Helper method for thread sleep.
     *
     * @param milliseconds Time to sleep
     */
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
