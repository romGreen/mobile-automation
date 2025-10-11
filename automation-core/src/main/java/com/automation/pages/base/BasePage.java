package com.automation.pages.base;

import com.automation.core.config.ConfigReader;
import com.automation.pages.components.NavigationBar;
import com.automation.pages.locators.BugTrackerLocators;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract base class for all Page Objects in the framework.
 *
 * For form-specific functionality (text input, dropdowns, date pickers, file attachment),
 * see BaseFormPage which extends this class.
 *
 * @author Rom
 * @version 1.0
 */
public abstract class BasePage {

    protected final Logger log = LogManager.getLogger(getClass());
    protected final AndroidDriver driver;
    protected final WaitHelper waitHelper;
    protected final GestureHelper gestureHelper;
    protected final ConfigReader configReader;
    public final NavigationBar navigationBar;

    /**
     * Hebrew month names for date picker operations.
     * Used by getMonthNumberFromHebrew() and getHebrewMonthName().
     */
    private static final String[] HEBREW_MONTHS = {
            "ינואר",
            "פברואר",
            "מרץ",
            "אפריל",
            "מאי",
            "יוני",
            "יולי",
            "אוגוסט",
            "ספטמבר",
            "אוקטובר",
            "נובמבר",
            "דצמבר"
    };

    /**
     * Constructs a BasePage
     *
     * @param driver         The AndroidDriver instance
     * @param waitHelper     Helper for wait operations
     * @param gestureHelper  Helper for gesture operations
     * @param configReader   Configuration reader for accessing settings
     */
    protected BasePage(AndroidDriver driver,
                       WaitHelper waitHelper,
                       GestureHelper gestureHelper,
                       ConfigReader configReader) {
        this.driver = driver;
        this.waitHelper = waitHelper;
        this.gestureHelper = gestureHelper;
        this.configReader = configReader;
        this.navigationBar = new NavigationBar(driver, waitHelper, gestureHelper, configReader);

        log.debug("Initialized page: {}", getClass().getSimpleName());
    }

    /**
     * Checks if the page is loaded.
     * This method must be implemented by subclasses to define page load implementation.
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
     * Navigates back using the device back button.
     */
    protected void navigateBack() {
        log.debug("Navigating back");
        driver.navigate().back();
    }

    /**
     * Sleeps for the custom duration.
     *
     * @param milliseconds sleeping time in milliseconds
     */
    protected void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Sleep interrupted", e);
        }
    }

    // ===== Element Presence Helper Methods =====

    /**
     * Checks if an element with the given resource ID is present on screen.
     *
     * @param resourceId The resource ID to check (e.g., BugTrackerLocators.BUG_ID_FIELD)
     * @return true if element is present, false otherwise
     */
    protected boolean isElementPresentById(String resourceId) {
        return !driver.findElements(
                AppiumBy.androidUIAutomator(
                        BugTrackerLocators.uiSelectorById(resourceId)
                )).isEmpty();
    }

    /**
     * Checks if an element with the given text is present on screen.
     *
     * @param text The text to check
     * @return true if element is present, false otherwise
     */
    protected boolean isElementPresentByText(String text) {
        return !driver.findElements(
                AppiumBy.androidUIAutomator(
                        BugTrackerLocators.uiSelectorByText(text)
                )).isEmpty();
    }

    /**
     * Checks if any of the given resource IDs is present on screen.
     * Useful for pages that can load in different scroll positions.
     *
     * @param resourceIds Array of resource IDs to check
     * @return true if at least one element is present, false otherwise
     */
    protected boolean isAnyElementPresentById(String... resourceIds) {
        for (String resourceId : resourceIds) {
            if (isElementPresentById(resourceId)) {
                return true;
            }
        }
        return false;
    }

    // ===== Hebrew Date Utilities =====

    /**
     * Converts Hebrew month name to month number.
     *
     * @param hebrewMonth Hebrew month name
     * @return Month number (1-12), or -1 if not found
     */
    protected int getMonthNumberFromHebrew(String hebrewMonth) {
        log.debug("Looking for Hebrew month: '{}' (length: {})", hebrewMonth, hebrewMonth.length());

        for (int i = 0; i < HEBREW_MONTHS.length; i++) {
            log.debug("Comparing with HEBREW_MONTHS[{}]: '{}' (length: {})", i, HEBREW_MONTHS[i], HEBREW_MONTHS[i].length());
            if (HEBREW_MONTHS[i].equals(hebrewMonth)) {
                log.debug("Match found! Month number: {}", i + 1);
                return i + 1;
            }
        }

        log.warn("No match found for Hebrew month: '{}'", hebrewMonth);
        return -1;
    }

    /**
     * Converts month number to Hebrew month name.
     *
     * @param monthNumber Month number (1-12)
     * @return Hebrew month name
     */
    protected String getHebrewMonthName(int monthNumber) {
        if (monthNumber >= 1 && monthNumber <= 12) {
            return HEBREW_MONTHS[monthNumber - 1];
        } else {
            log.warn("Invalid month number: {}, returning January", monthNumber);
            return HEBREW_MONTHS[0];
        }
    }
}
