package com.automation.pages.bugtracker;

import com.automation.core.context.MobileContextManager;
import com.automation.enums.BugPriority;
import com.automation.enums.BugSeverity;
import com.automation.enums.BugStatus;
import com.automation.pages.base.NativePage;
import com.automation.pages.locators.BugTrackerLocators;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Page Object for the Edit Bug screen.
 *
 * This page handles all interactions with the bug editing form.
 * The edit form is WebView-based with different field IDs than the create form.
 *
 * Demonstrates OOP principles:
 * - Inheritance: Extends NativePage
 * - Encapsulation: Hides form interaction complexity
 * - Single Responsibility: Manages only edit bug form
 * - Separation of Concerns: Separate from create form
 *
 * @author Automation Team
 * @version 1.0
 */
@Component
@Scope("prototype")
public class EditBugPage extends NativePage {

    /**
     * Constructs an EditBugPage with dependency injection.
     *
     * @param driver         The AndroidDriver instance
     * @param waitHelper     Helper for wait operations
     * @param gestureHelper  Helper for gesture operations
     * @param contextManager Manager for context switching
     */
    @Autowired
    public EditBugPage(AndroidDriver driver,
                       WaitHelper waitHelper,
                       GestureHelper gestureHelper,
                       MobileContextManager contextManager) {
        super(driver, waitHelper, gestureHelper, contextManager);
        waitForFormToLoad();
    }

    @Override
    public boolean isLoaded() {
        // Try WebView context first
        boolean inWebView = contextManager.trySwitchToWebView(
                driver, BugTrackerLocators.APP_PACKAGE, 3000);

        if (inWebView) {
            if (!driver.findElements(By.id(BugTrackerLocators.WEB_EDIT_PAGE_ID)).isEmpty()) {
                return true;
            }
        }

        // Fall back to native context check
        ensureNativeContext();
        return !driver.findElements(AppiumBy.androidUIAutomator(
                BugTrackerLocators.uiSelectorById(BugTrackerLocators.EDIT_BUG_TITLE_FIELD))).isEmpty();
    }

    /**
     * Waits for the edit form to be fully loaded.
     */
    private void waitForFormToLoad() {
        log.info("Waiting for Edit Bug Form to load");
        sleep(2000);

        // Use native context
        ensureNativeContext();

        try {
            // Wait for edit bug title field to be present using UiAutomator
            waitHelper.createWait(10)
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                            AppiumBy.androidUIAutomator(
                                    BugTrackerLocators.uiSelectorById(BugTrackerLocators.EDIT_BUG_TITLE_FIELD))));
            log.info("Edit Bug Form loaded successfully");
        } catch (Exception e) {
            log.error("Edit Bug Form did not load within timeout", e);
            throw new IllegalStateException("Edit Bug Form did not load within timeout", e);
        }
    }

    /**
     * Selects bug status from dropdown.
     *
     * @param status The status to select
     * @return this page object for method chaining
     */
    public EditBugPage selectStatus(BugStatus status) {
        log.debug("Selecting status: {}", status);

        // Use native context
        ensureNativeContext();

        // Scroll left to ensure we're at the start of the form
        gestureHelper.scrollLeft();
        sleep(500);

        // Scroll down to see the status field
        gestureHelper.scrollDown();
        sleep(500);

        try {
            // Find and click on the status field using UiAutomator
            var statusField = waitHelper.createWait(10)
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                            AppiumBy.androidUIAutomator(
                                    BugTrackerLocators.uiSelectorById(BugTrackerLocators.EDIT_BUG_STATUS_FIELD))));

            statusField.click();
            sleep(800);

            // Click on the desired status option
            var option = driver.findElement(
                    AppiumBy.androidUIAutomator(
                            BugTrackerLocators.uiSelectorByText(status.getDisplayName())));
            option.click();
            sleep(300);

            log.debug("Selected status: {}", status.getDisplayName());
        } catch (Exception e) {
            log.error("Failed to select status: {}", status, e);
            throw new RuntimeException("Failed to select status: " + status, e);
        }

        return this;
    }

    /**
     * Selects bug severity from dropdown.
     *
     * @param severity The severity to select
     * @return this page object for method chaining
     */
    public EditBugPage selectSeverity(BugSeverity severity) {
        log.debug("Selecting severity: {}", severity);

        contextManager.trySwitchToWebView(driver, BugTrackerLocators.APP_PACKAGE, 5000);

        try {
            WebElement severityField = waitHelper.createWait(10)
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                            By.id(BugTrackerLocators.EDIT_BUG_SEVERITY_FIELD)));

            severityField.click();
            sleep(800);

            WebElement option = driver.findElement(
                    AppiumBy.xpath("//*[contains(text(), '" + severity.getDisplayName() + "')]"));
            option.click();
            sleep(300);

            log.debug("Selected severity: {}", severity.getDisplayName());
        } catch (Exception e) {
            log.error("Failed to select severity: {}", severity, e);
            throw new RuntimeException("Failed to select severity: " + severity, e);
        }

        return this;
    }

    /**
     * Selects bug priority from dropdown.
     *
     * @param priority The priority to select
     * @return this page object for method chaining
     */
    public EditBugPage selectPriority(BugPriority priority) {
        log.debug("Selecting priority: {}", priority);

        contextManager.trySwitchToWebView(driver, BugTrackerLocators.APP_PACKAGE, 5000);

        try {
            WebElement priorityField = waitHelper.createWait(10)
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                            By.id(BugTrackerLocators.EDIT_BUG_PRIORITY_FIELD)));

            priorityField.click();
            sleep(800);

            WebElement option = driver.findElement(
                    AppiumBy.xpath("//*[contains(text(), '" + priority.getDisplayName() + "')]"));
            option.click();
            sleep(300);

            log.debug("Selected priority: {}", priority.getDisplayName());
        } catch (Exception e) {
            log.error("Failed to select priority: {}", priority, e);
            throw new RuntimeException("Failed to select priority: " + priority, e);
        }

        return this;
    }

    /**
     * Sets the bug title.
     *
     * @param title Bug title text
     * @return this page object for method chaining
     */
    public EditBugPage setTitle(String title) {
        log.debug("Setting title: {}", title);

        contextManager.trySwitchToWebView(driver, BugTrackerLocators.APP_PACKAGE, 5000);

        WebElement field = waitHelper.createWait(10)
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                        By.id(BugTrackerLocators.EDIT_BUG_TITLE_FIELD)));

        clearAndType(field, title);
        return this;
    }

    /**
     * Sets the steps to reproduce.
     *
     * @param steps Steps to reproduce the bug
     * @return this page object for method chaining
     */
    public EditBugPage setStepsToReproduce(String steps) {
        log.debug("Setting steps to reproduce");

        contextManager.trySwitchToWebView(driver, BugTrackerLocators.APP_PACKAGE, 5000);

        WebElement field = waitHelper.createWait(10)
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                        By.id(BugTrackerLocators.EDIT_BUG_STEPS_FIELD)));

        clearAndType(field, steps);
        return this;
    }

    /**
     * Sets the expected result.
     *
     * @param expected Expected result text
     * @return this page object for method chaining
     */
    public EditBugPage setExpectedResult(String expected) {
        log.debug("Setting expected result");

        contextManager.trySwitchToWebView(driver, BugTrackerLocators.APP_PACKAGE, 5000);

        WebElement field = waitHelper.createWait(10)
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                        By.id(BugTrackerLocators.EDIT_BUG_EXPECTED_RESULT_FIELD)));

        clearAndType(field, expected);
        return this;
    }

    /**
     * Sets the actual result.
     *
     * @param actual Actual result text
     * @return this page object for method chaining
     */
    public EditBugPage setActualResult(String actual) {
        log.debug("Setting actual result");

        contextManager.trySwitchToWebView(driver, BugTrackerLocators.APP_PACKAGE, 5000);

        WebElement field = waitHelper.createWait(10)
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                        By.id(BugTrackerLocators.EDIT_BUG_ACTUAL_RESULT_FIELD)));

        clearAndType(field, actual);
        return this;
    }

    /**
     * Sets the detected by field.
     *
     * @param detectedBy Name of person who detected the bug
     * @return this page object for method chaining
     */
    public EditBugPage setDetectedBy(String detectedBy) {
        log.debug("Setting detected by: {}", detectedBy);

        contextManager.trySwitchToWebView(driver, BugTrackerLocators.APP_PACKAGE, 5000);

        WebElement field = waitHelper.createWait(10)
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                        By.id(BugTrackerLocators.EDIT_BUG_DETECTED_BY_FIELD)));

        clearAndType(field, detectedBy);
        return this;
    }

    /**
     * Sets the fixed by field.
     *
     * @param fixedBy Name of person who fixed the bug
     * @return this page object for method chaining
     */
    public EditBugPage setFixedBy(String fixedBy) {
        log.debug("Setting fixed by: {}", fixedBy);

        contextManager.trySwitchToWebView(driver, BugTrackerLocators.APP_PACKAGE, 5000);

        WebElement field = waitHelper.createWait(10)
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                        By.id(BugTrackerLocators.EDIT_BUG_FIXED_BY_FIELD)));

        clearAndType(field, fixedBy);
        return this;
    }

    /**
     * Gets the current status value.
     *
     * @return Current status display name
     */
    public String getStatus() {
        log.debug("Getting current status");

        contextManager.trySwitchToWebView(driver, BugTrackerLocators.APP_PACKAGE, 5000);

        WebElement statusField = waitHelper.createWait(10)
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                        By.id(BugTrackerLocators.EDIT_BUG_STATUS_FIELD)));

        return statusField.getText();
    }

    /**
     * Submits the edit form by clicking "Save Changes".
     *
     * @return BugsListPage after successful submission
     */
    public BugsListPage submit() {
        log.info("Submitting edit bug form");

        // Use native context
        ensureNativeContext();

        // Hide keyboard
        try {
            driver.hideKeyboard();
            sleep(300);
        } catch (Exception ignored) {}

        // Scroll down to ensure Save Changes button is visible
        gestureHelper.scrollDown();
        sleep(500);

        try {
            // Find and click "Save Changes" button using UiAutomator
            var saveButton = waitHelper.createWait(10)
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                            AppiumBy.androidUIAutomator(
                                    BugTrackerLocators.uiSelectorByText(BugTrackerLocators.SAVE_CHANGES_BUTTON_TEXT))));

            saveButton.click();
            log.info("Clicked 'Save Changes' button");

            sleep(1500);

            return new BugsListPage(driver, waitHelper, gestureHelper, contextManager);

        } catch (Exception e) {
            log.error("Could not find or click 'Save Changes' button", e);
            throw new IllegalStateException("Could not find submit button: Save Changes", e);
        }
    }

    // ===== Private Helper Methods =====

    /**
     * Clears a field and types new text.
     *
     * @param field The WebElement to type into
     * @param text  The text to type
     */
    private void clearAndType(WebElement field, String text) {
        field.click();
        sleep(300);

        try {
            field.clear();
            sleep(200);
        } catch (Exception e) {
            log.debug("Could not clear field: {}", e.getMessage());
        }

        field.sendKeys(text);
        sleep(300);
        log.debug("Set value: {}", text);
    }
}
