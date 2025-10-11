package com.automation.pages.components;

import com.automation.core.config.ConfigReader;
import com.automation.pages.bugtracker.CreateBugPage;
import com.automation.pages.bugtracker.BugsListPage;
import com.automation.pages.bugtracker.HomePage;
import com.automation.pages.locators.BugTrackerLocators;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Component class representing the navigation bar on all screens.
 *
 * The navigation bar contains three tabs:
 * - Home
 * - Create Bug
 * - View Bugs
 *
 * Important: Tabs may be scrolled off-screen. This class:
 * - Checks if tab is visible before scrolling
 * - Scrolls up only if tab is not visible
 * - Avoids over-scrolling (which triggers refresh and loses form data)
 *
 * @author Rom
 * @version 1.0
 */
@Component
@Scope("prototype")
public class NavigationBar {

    private static final Logger log = LogManager.getLogger(NavigationBar.class);

    private final AndroidDriver driver;
    private final WaitHelper waitHelper;
    private final GestureHelper gestureHelper;
    private final ConfigReader configReader;

    @Autowired
    public NavigationBar(AndroidDriver driver,
                         WaitHelper waitHelper,
                         GestureHelper gestureHelper,
                         ConfigReader configReader) {
        this.driver = driver;
        this.waitHelper = waitHelper;
        this.gestureHelper = gestureHelper;
        this.configReader = configReader;
    }

    /**
     * Navigates to Home screen by tapping the Home tab.
     *
     * @return HomePage instance
     */
    public HomePage goToHome() {
        log.info("Navigating to Home screen");

        ensureTabIsVisible(BugTrackerLocators.HOME_TEXT);

        var homeTab = waitHelper.createWait(10)
                .until(ExpectedConditions.elementToBeClickable(
                        AppiumBy.androidUIAutomator(
                                BugTrackerLocators.uiSelectorByText(BugTrackerLocators.HOME_TEXT))));

        homeTab.click();
        log.info("Clicked Home tab");
        sleep(1000);

        return new HomePage(driver, waitHelper, gestureHelper, configReader);
    }

    /**
     * Navigates to Create Bug screen by tapping the Create Bug tab.
     *
     * @return CreateBugPage instance
     */
    public CreateBugPage goToCreateBug() {
        log.info("Navigating to Create Bug screen");

        ensureTabIsVisible(BugTrackerLocators.CREATE_BUG_TEXT);

        var createBugTab = waitHelper.createWait(10)
                .until(ExpectedConditions.elementToBeClickable(
                        AppiumBy.accessibilityId(BugTrackerLocators.CREATE_BUG_ACCESSIBILITY_ID)));

        createBugTab.click();
        log.info("Clicked Create Bug tab");
        sleep(1500);

        return new CreateBugPage(driver, waitHelper, gestureHelper, configReader);
    }

    /**
     * Navigates to View Bugs screen by tapping the View Bugs tab.
     *
     * Uses enhanced click strategy with fallback to coordinate-based tapping
     * if standard click doesn't work.
     *
     * @return BugsListPage instance
     */
    public BugsListPage goToViewBugs() {
        log.info("Navigating to View Bugs screen");

        try {
            // Hide keyboard to stabilize layout
            try {
                driver.hideKeyboard();
                sleep(300);
            } catch (Exception ignored) {}

            // Ensure tab is visible
            ensureTabIsVisible(BugTrackerLocators.VIEW_BUGS_TEXT);

            // First attempt: Click by accessibility ID
            try {
                var viewBugsButton = waitHelper.createWait(10)
                        .until(ExpectedConditions.elementToBeClickable(
                                AppiumBy.accessibilityId(BugTrackerLocators.VIEW_BUGS_ACCESSIBILITY_ID)));

                viewBugsButton.click();
                log.info("Clicked 'View Bugs' tab using accessibility ID");
                sleep(1000);

                // Check if focus changed from Create Bug
                var createBugButton = driver.findElement(
                        AppiumBy.accessibilityId(BugTrackerLocators.CREATE_BUG_ACCESSIBILITY_ID));
                String isFocused = createBugButton.getAttribute("focused");

                if (!"true".equals(isFocused)) {
                    // Success - focus changed away from Create Bug
                    log.info("Successfully navigated to View Bugs tab");
                    sleep(1500);
                    return new BugsListPage(driver, waitHelper, gestureHelper, configReader);
                }

                log.warn("Focus did not change - trying fallback tap at safe coordinates");

            } catch (Exception e) {
                log.warn("Accessibility ID click failed: {}", e.getMessage());
            }

            // Fallback: Tap at safe coordinates (right-inner area because maybe the screen is too small so the buttons overlap)
            var viewBugsElement = driver.findElement(
                    AppiumBy.accessibilityId(BugTrackerLocators.VIEW_BUGS_ACCESSIBILITY_ID));

            org.openqa.selenium.Rectangle rect = viewBugsElement.getRect();

            // Tap at 85% from left (right-inner side) and middle vertically
            int safeX = rect.getX() + (int)(rect.getWidth() * 0.85);
            int safeY = rect.getY() + rect.getHeight() / 2;

            log.info("Tapping View Bugs at safe coordinates ({}, {})", safeX, safeY);

            org.openqa.selenium.interactions.PointerInput finger = new org.openqa.selenium.interactions.PointerInput(
                    org.openqa.selenium.interactions.PointerInput.Kind.TOUCH, "finger");
            org.openqa.selenium.interactions.Sequence tap = new org.openqa.selenium.interactions.Sequence(finger, 0);

            tap.addAction(finger.createPointerMove(java.time.Duration.ZERO,
                    org.openqa.selenium.interactions.PointerInput.Origin.viewport(), safeX, safeY));
            tap.addAction(finger.createPointerDown(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(finger.createPointerUp(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));

            driver.perform(java.util.Arrays.asList(tap));

            log.info("Clicked 'View Bugs' tab at safe coordinates");
            sleep(1500);

            return new BugsListPage(driver, waitHelper, gestureHelper, configReader);

        } catch (Exception e) {
            log.error("Failed to navigate to View Bugs tab", e);
            throw new IllegalStateException("Could not navigate to View Bugs tab: " + e.getMessage(), e);
        }
    }

    /**
     * Ensures the navigation tab is visible by scrolling up if needed.
     * Scrolls up to 3 times, checking visibility after each scroll to minimize scrolling.
     *
     * @param tabText The text of the tab to make visible
     */
    private void ensureTabIsVisible(String tabText) {
        log.debug("Ensuring tab is visible: {}", tabText);

        // First, check if the navigation tab is already visible
        if (isTabVisible(tabText)) {
            log.debug("Tab already visible: {}", tabText);
            return;
        }

        log.debug("Tab not visible, scrolling up to reveal navigation bar");

        int maxScrollAttempts = 3;
        for (int i = 1; i <= maxScrollAttempts; i++) {
            log.debug("Scroll attempt {} to reveal navigation bar", i);
            gestureHelper.scrollUp();
            sleep(500);

            // Check if tab is now visible
            if (isTabVisible(tabText)) {
                log.debug("Tab is now visible after {} scroll(s)", i);
                return;
            }
        }

        // Tab still not visible after multiple scrolls
        log.error("Tab '{}' still not visible after {} scroll attempts. Navigation bar might be hidden.",
                tabText, maxScrollAttempts);
        throw new IllegalStateException(
                String.format("Navigation tab '%s' is not accessible after %d scroll attempts. " +
                        "Navigation bar may be hidden or app is in unexpected state.", tabText, maxScrollAttempts));
    }

    /**
     * Checks if a navigation tab is currently visible on screen.
     *
     * @param tabText The text of the tab to check
     * @return true if tab is visible, false otherwise
     */
    private boolean isTabVisible(String tabText) {
        try {
            var elements = driver.findElements(
                    AppiumBy.androidUIAutomator(
                            BugTrackerLocators.uiSelectorByText(tabText)));

            if (elements.isEmpty()) {
                return false;
            }
            // Check if element is actually displayed (not just present in DOM)
            return elements.get(0).isDisplayed();

        } catch (Exception e) {
            log.debug("Error checking tab visibility: {}", e.getMessage());
            return false;
        }
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Sleep interrupted", e);
        }
    }
}
