package com.automation.pages.base;

import com.automation.core.context.MobileContextManager;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract base class for all Page Objects in the framework.
 *
 * This class provides common functionality shared by all pages:
 * - Driver access
 * - Wait operations
 * - Gesture operations
 * - Context switching
 * - Logging
 *
 * Demonstrates OOP principles:
 * - Abstraction: Defines common page behavior
 * - Inheritance: All page classes extend this
 * - Encapsulation: Protects driver and helpers
 * - Code reuse: Common methods available to all pages
 *
 * @author Automation Team
 * @version 1.0
 */
public abstract class BasePage {

    protected final Logger log = LogManager.getLogger(getClass());
    protected final AndroidDriver driver;
    protected final WaitHelper waitHelper;
    protected final GestureHelper gestureHelper;
    protected final MobileContextManager contextManager;

    /**
     * Constructs a BasePage with required dependencies.
     *
     * @param driver         The AndroidDriver instance
     * @param waitHelper     Helper for wait operations
     * @param gestureHelper  Helper for gesture operations
     * @param contextManager Manager for context switching
     */
    protected BasePage(AndroidDriver driver,
                       WaitHelper waitHelper,
                       GestureHelper gestureHelper,
                       MobileContextManager contextManager) {
        this.driver = driver;
        this.waitHelper = waitHelper;
        this.gestureHelper = gestureHelper;
        this.contextManager = contextManager;

        log.debug("Initialized page: {}", getClass().getSimpleName());
    }

    /**
     * Checks if the page is currently loaded.
     * This method must be implemented by subclasses to define page-specific load criteria.
     *
     * @return true if page is loaded, false otherwise
     */
    public abstract boolean isLoaded();

    /**
     * Waits for the page to be fully loaded.
     * Default implementation uses isLoaded() with polling.
     * Subclasses can override for custom loading logic.
     *
     * @param timeoutMs Maximum time to wait in milliseconds
     * @return this page object for method chaining
     * @throws IllegalStateException if page does not load within timeout
     */
    public BasePage waitUntilLoaded(long timeoutMs) {
        long endTime = System.currentTimeMillis() + timeoutMs;

        while (System.currentTimeMillis() < endTime) {
            if (isLoaded()) {
                log.info("Page loaded: {}", getClass().getSimpleName());
                return this;
            }
            sleep(250);
        }

        String pageSource = driver.getPageSource();
        String preview = pageSource.substring(0, Math.min(800, pageSource.length()));

        throw new IllegalStateException(
                String.format("Page '%s' did not load within %d ms. Page source preview: %s",
                        getClass().getSimpleName(), timeoutMs, preview));
    }

    /**
     * Gets the current page source for debugging.
     *
     * @return The page source XML/HTML
     */
    protected String getPageSource() {
        return driver.getPageSource();
    }

    /**
     * Navigates back using the device back button.
     */
    protected void navigateBack() {
        log.debug("Navigating back");
        driver.navigate().back();
    }

    /**
     * Sleeps for the specified duration.
     * Use sparingly - prefer explicit waits.
     *
     * @param milliseconds Time to sleep
     */
    protected void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Sleep interrupted", e);
        }
    }

    /**
     * Takes a screenshot and returns it as base64.
     *
     * @return Screenshot as base64 string
     */
    protected String captureScreenshot() {
        return driver.getScreenshotAs(org.openqa.selenium.OutputType.BASE64);
    }

    /**
     * Hides the keyboard if it's currently shown.
     */
    protected void hideKeyboard() {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
                log.debug("Keyboard hidden");
            }
        } catch (Exception e) {
            log.debug("Could not hide keyboard (might not be shown)", e);
        }
    }


}
