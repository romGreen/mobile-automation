package com.automation.pages.bugtracker;

import com.automation.core.context.MobileContextManager;
import com.automation.pages.base.NativePage;
import com.automation.pages.locators.BugTrackerLocators;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Page Object for the Bugs List/Home screen.
 *
 * This page displays the list of bugs and provides navigation to create new bugs.
 * It uses both Native and WebView elements.
 *
 * Demonstrates OOP principles:
 * - Inheritance: Extends NativePage
 * - Encapsulation: Hides page interaction complexity
 * - Single Responsibility: Manages only bugs list page
 * - Method chaining: Fluent API for readable tests
 *
 * @author Automation Team
 * @version 1.0
 */
@Component
@Scope("prototype")
public class BugsListPage extends NativePage {

    /**
     * Constructs a BugsListPage with dependency injection.
     *
     * @param driver         The AndroidDriver instance
     * @param waitHelper     Helper for wait operations
     * @param gestureHelper  Helper for gesture operations
     * @param contextManager Manager for context switching
     */
    @Autowired
    public BugsListPage(AndroidDriver driver,
                        WaitHelper waitHelper,
                        GestureHelper gestureHelper,
                        MobileContextManager contextManager) {
        super(driver, waitHelper, gestureHelper, contextManager);
    }

    @Override
    public boolean isLoaded() {
        // Check for native toolbar OR WebView presence
        ensureNativeContext();

        boolean hasToolbar = !driver.findElements(
                AppiumBy.id(BugTrackerLocators.TOOLBAR_ID)).isEmpty();

        boolean hasContainer = !driver.findElements(
                AppiumBy.id(BugTrackerLocators.HOME_CONTAINER_ID)).isEmpty();

        boolean hasWebView = !driver.findElements(
                AppiumBy.className("android.webkit.WebView")).isEmpty();

        return hasToolbar || hasContainer || hasWebView;
    }

    /**
     * Navigates to the Create Bug form by tapping the Create Bug button.
     *
     * This method handles the complexity of finding and clicking the button,
     * which may be accessible via different strategies depending on the app state.
     *
     * @return BugFormPage instance
     * @throws com.automation.exceptions.ElementNotFoundException if button cannot be found
     */
    public BugFormPage tapCreateBug() {
        log.info("Navigating to Create Bug screen");

        try {
            // The Create Bug button is accessible via accessibility ID
            var createButton = waitHelper.createWait(10)
                    .until(ExpectedConditions.elementToBeClickable(
                            AppiumBy.accessibilityId(BugTrackerLocators.CREATE_BUG_ACCESSIBILITY_ID)));

            createButton.click();
            log.info("Clicked 'Create Bug' button");

            // Wait for form to load
            sleep(1500);

            // Return new BugFormPage instance (Spring will inject dependencies)
            return new BugFormPage(driver, waitHelper, gestureHelper, contextManager);

        } catch (Exception e) {
            log.error("Failed to tap 'Create Bug' button", e);

            // Provide helpful debugging information
            String availableElements = getAvailableClickableElements();
            log.error("Available clickable elements:\n{}", availableElements);

            throw new com.automation.exceptions.ElementNotFoundException(
                    "Create Bug button",
                    "accessibilityId",
                    String.format("Button not found. Available elements:\n%s", availableElements)
            );
        }
    }

    /**
     * Navigates to the View Bugs tab if not already there.
     *
     * This method tries to switch to WebView context, but falls back
     * to native context if WebView is not available.
     *
     * @return this page object for method chaining
     */
    public BugsListPage goToViewTab() {
        // Try to switch to WebView context (non-throwing version)
        boolean inWebView = contextManager.trySwitchToWebView(
                driver, BugTrackerLocators.APP_PACKAGE, 5000);

        if (!inWebView) {
            log.warn("WebView not available, staying in native context");
            ensureNativeContext();
            return this;
        }

        By viewTabLocator = AppiumBy.xpath(
                "//*[normalize-space()='" + BugTrackerLocators.VIEW_BUGS_TEXT + "']");

        By viewPageLocator = By.id(BugTrackerLocators.WEB_VIEW_PAGE_ID);

        // Check if already on View tab
        if (driver.findElements(viewPageLocator).isEmpty()) {
            log.info("Switching to View Bugs tab");
            waitHelper.createWait(5)
                    .until(ExpectedConditions.elementToBeClickable(viewTabLocator))
                    .click();
            sleep(500);
        } else {
            log.debug("Already on View Bugs tab");
        }

        return this;
    }

    /**
     * Waits for a bug with the specified title to appear in the list.
     *
     * This method is useful for verifying that a newly created bug
     * appears in the bugs list after submission.
     *
     * @param title The bug title to wait for
     * @return this page object for method chaining
     * @throws TimeoutException if bug is not found within timeout
     */
    public BugsListPage waitForBugWithTitle(String title) {
        log.info("Waiting for bug with title: {}", title);

        // Try to switch to WebView, but continue in native if not available
        boolean inWebView = contextManager.trySwitchToWebView(
                driver, BugTrackerLocators.APP_PACKAGE, 5000);

        if (!inWebView) {
            log.warn("WebView not available for bug verification, using native context");
            ensureNativeContext();
        }

        // First try to find within View page
        By inViewPageLocator = AppiumBy.xpath(
                "//*[@id='" + BugTrackerLocators.WEB_VIEW_PAGE_ID + "']" +
                "//*[contains(normalize-space(.), '" + title + "')]");

        try {
            waitHelper.createWait(15)
                    .until(ExpectedConditions.presenceOfElementLocated(inViewPageLocator));
            log.info("Bug found in list: {}", title);
            return this;
        } catch (TimeoutException e) {
            // Try finding anywhere on page as fallback
            log.debug("Bug not found in view page, trying anywhere on page");

            By anywhereLocator = AppiumBy.xpath(
                    "//*[contains(normalize-space(.), '" + title + "')]");

            waitHelper.createWait(15)
                    .until(ExpectedConditions.presenceOfElementLocated(anywhereLocator));

            log.info("Bug found on page (outside view container): {}", title);
            return this;
        }
    }

    /**
     * Gets available clickable elements for debugging.
     * Used when navigation fails to provide helpful error messages.
     *
     * @return String describing available clickable elements
     */
    private String getAvailableClickableElements() {
        try {
            ensureNativeContext();

            var clickableElements = driver.findElements(
                    AppiumBy.androidUIAutomator(BugTrackerLocators.uiSelectorClickable()));

            StringBuilder sb = new StringBuilder();
            int count = Math.min(10, clickableElements.size());

            for (int i = 0; i < count; i++) {
                var element = clickableElements.get(i);
                sb.append(String.format("  [%d] desc='%s', text='%s'%n",
                        i,
                        element.getAttribute("content-desc"),
                        element.getAttribute("text")));
            }

            return sb.toString();

        } catch (Exception e) {
            return "Could not retrieve elements: " + e.getMessage();
        }
    }
}
