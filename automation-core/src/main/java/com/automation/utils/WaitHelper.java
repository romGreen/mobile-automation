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
 * @author Rom
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
     * Constructs a WaitHelper with the given driver.
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
     * @param driver The AndroidDriver instance
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
     * Creates a new WebDriverWait with custom timeout.
     *
     * @param timeoutSeconds Custom timeout in seconds
     * @return New WebDriverWait instance
     */
    public WebDriverWait createWait(int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }
}
