package com.automation.utils;

import com.automation.exceptions.ElementNotFoundException;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Utility class providing explicit wait operations for mobile automation.
 *
 * This class encapsulates all wait-related logic, providing reusable
 * wait methods with proper error handling and logging.
 *
 * Demonstrates OOP principles:
 * - Single Responsibility: Handles only wait operations
 * - Encapsulation: Hides WebDriverWait complexity
 * - Reusability: Used across all page objects
 *
 * @author Automation Team
 * @version 1.0
 */
@Component
@Scope("prototype")
public class WaitHelper {

    private static final Logger log = LogManager.getLogger(WaitHelper.class);
    private static final int DEFAULT_TIMEOUT_SECONDS = 15;

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    /**
     * Constructs a WaitHelper with the given driver (injected by Spring).
     *
     * @param driver The AndroidDriver instance
     */
    @Autowired
    public WaitHelper(AndroidDriver driver) {
        this(driver, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * Constructs a WaitHelper with custom timeout.
     *
     * @param driver         The AndroidDriver instance
     * @param timeoutSeconds Timeout in seconds for wait operations
     */
    public WaitHelper(AndroidDriver driver, int timeoutSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * Waits for an element to be visible by its locator.
     *
     * @param by Element locator
     * @return The visible WebElement
     * @throws ElementNotFoundException if element is not found within timeout
     */
    public WebElement waitForVisibility(By by) {
        try {
            log.debug("Waiting for element visibility: {}", by);
            return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(
                    by.toString(),
                    "visibility",
                    "Element was not visible within timeout");
        }
    }

    /**
     * Waits for an element to be clickable by its locator.
     *
     * @param by Element locator
     * @return The clickable WebElement
     * @throws ElementNotFoundException if element is not clickable within timeout
     */
    public WebElement waitForClickable(By by) {
        try {
            log.debug("Waiting for element to be clickable: {}", by);
            return wait.until(ExpectedConditions.elementToBeClickable(by));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(
                    by.toString(),
                    "clickable",
                    "Element was not clickable within timeout");
        }
    }

    /**
     * Waits for an element to be present in the DOM (not necessarily visible).
     *
     * @param by Element locator
     * @return The present WebElement
     * @throws ElementNotFoundException if element is not present within timeout
     */
    public WebElement waitForPresence(By by) {
        try {
            log.debug("Waiting for element presence: {}", by);
            return wait.until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(
                    by.toString(),
                    "presence",
                    "Element was not present in DOM within timeout");
        }
    }

    /**
     * Waits for an element to disappear from the page.
     *
     * @param by Element locator
     * @return true if element disappeared, false otherwise
     */
    public boolean waitForInvisibility(By by) {
        try {
            log.debug("Waiting for element invisibility: {}", by);
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            log.warn("Element did not become invisible within timeout: {}", by);
            return false;
        }
    }

    /**
     * Waits for an element identified by accessibility ID.
     *
     * @param accessibilityId The accessibility ID of the element
     * @return The visible WebElement
     * @throws ElementNotFoundException if element is not found
     */
    public WebElement waitForAccessibilityId(String accessibilityId) {
        return waitForVisibility(AppiumBy.accessibilityId(accessibilityId));
    }

    /**
     * Waits for an element identified by resource ID.
     *
     * @param resourceId The resource ID of the element
     * @return The visible WebElement
     * @throws ElementNotFoundException if element is not found
     */
    public WebElement waitForId(String resourceId) {
        return waitForVisibility(AppiumBy.id(resourceId));
    }

    /**
     * Waits for an element using UiAutomator selector.
     *
     * @param uiAutomatorSelector The UiAutomator selector string
     * @return The visible WebElement
     * @throws ElementNotFoundException if element is not found
     */
    public WebElement waitForUiAutomator(String uiAutomatorSelector) {
        return waitForVisibility(AppiumBy.androidUIAutomator(uiAutomatorSelector));
    }

    /**
     * Waits for text to be present in an element.
     *
     * @param by   Element locator
     * @param text Text to wait for
     * @return true if text is present, false otherwise
     */
    public boolean waitForTextPresent(By by, String text) {
        try {
            log.debug("Waiting for text '{}' in element: {}", text, by);
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(by, text));
        } catch (TimeoutException e) {
            log.warn("Text '{}' not found in element {} within timeout", text, by);
            return false;
        }
    }

    /**
     * Performs a custom wait with a custom condition.
     *
     * @param condition The wait condition
     * @param <T>       The return type of the condition
     * @return The result of the wait condition
     */
    public <T> T waitUntil(org.openqa.selenium.support.ui.ExpectedCondition<T> condition) {
        return wait.until(condition);
    }

    /**
     * Creates a new WebDriverWait with custom timeout.
     *
     * @param timeoutSeconds Custom timeout in seconds
     * @return New WebDriverWait instance
     */
    public WebDriverWait createWait(int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * Sleeps for the specified duration (use sparingly, prefer explicit waits).
     *
     * @param milliseconds Time to sleep in milliseconds
     */
    public void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Sleep interrupted", e);
        }
    }
}
