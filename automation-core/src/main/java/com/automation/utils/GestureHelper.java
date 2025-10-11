package com.automation.utils;

import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;

/**
 * Utility class providing mobile gesture operations (tap, swipe, scroll...).
 * This class encapsulates W3C Actions for mobile gestures, providing
 * clean API for touch interactions.
 *
 * @author Rom
 * @version 1.0
 */
@Component
@Scope("prototype")
public class GestureHelper {

    private static final Logger log = LogManager.getLogger(GestureHelper.class);
    private static final String FINGER = "finger";

    private final AndroidDriver driver;

    /**
     * Constructs a GestureHelper with the given driver.
     *
     * @param driver The AndroidDriver instance
     */
    @Autowired
    public GestureHelper(AndroidDriver driver) {
        this.driver = driver;
    }

    /**
     * Performs a tap at specific coordinates.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void tapAt(int x, int y) {
        log.debug("Tapping at coordinates: ({}, {})", x, y);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, FINGER);
        Sequence tap = new Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(tap));
    }

    /**
     * Taps on a specific element.
     *
     * @param element The element to tap
     */
    public void tapElement(WebElement element) {
        log.debug("Tapping on element: {}", element);
        Point location = element.getLocation();
        Dimension size = element.getSize();

        // Tap at center of element
        int centerX = location.getX() + (size.getWidth() / 2);
        int centerY = location.getY() + (size.getHeight() / 2);

        tapAt(centerX, centerY);
    }

    /**
     * Performs a swipe with custom duration.
     *
     * @param startX     Start X coordinate
     * @param startY     Start Y coordinate
     * @param endX       End X coordinate
     * @param endY       End Y coordinate
     * @param durationMs Duration of swipe in milliseconds
     */
    public void swipe(int startX, int startY, int endX, int endY, long durationMs) {
        log.debug("Swiping from ({}, {}) to ({}, {}) in {} ms", startX, startY, endX, endY, durationMs);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, FINGER);
        Sequence swipe = new Sequence(finger, 1);

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(durationMs), PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(swipe));
    }

    public void scrollDown() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.getWidth() / 2;
        int startY = (int) (size.getHeight() * 0.8);
        int endY = (int) (size.getHeight() * 0.2);

        swipe(startX, startY, startX, endY, 800);
    }

    public void scrollUp() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.getWidth() / 2;
        int startY = (int) (size.getHeight() * 0.2);
        int endY = (int) (size.getHeight() * 0.8);

        swipe(startX, startY, startX, endY, 800);
    }

    public void scrollRight() {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.getWidth() * 0.8);
        int endX = (int) (size.getWidth() * 0.2);
        int startY = size.getHeight() / 2;

        swipe(startX, startY, endX, startY, 800);
    }

    public void scrollLeft() {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.getWidth() * 0.2);
        int endX = (int) (size.getWidth() * 0.8);
        int startY = size.getHeight() / 2;

        swipe(startX, startY, endX, startY, 800);
    }

    /**
     * Scrolls to a specific element.
     * @param resourceId The resource ID of the element to scroll to
     */
    public void scrollToElement(String resourceId) {
        log.debug("Scrolling to element with resource ID: {}", resourceId);
        try {
            String uiSelector = String.format("new UiSelector().resourceId(\"%s\")", resourceId);
            String scrollableSelector = "new UiSelector().scrollable(true)";
            String uiAutomatorCommand = String.format(
                    "new UiScrollable(%s).scrollIntoView(%s)",
                    scrollableSelector,
                    uiSelector
            );
            driver.findElement(io.appium.java_client.AppiumBy.androidUIAutomator(uiAutomatorCommand));
        } catch (Exception e) {
            log.debug("Could not scroll to element: {}", resourceId, e);
        }
    }
}
