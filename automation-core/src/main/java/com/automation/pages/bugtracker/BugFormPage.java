package com.automation.pages.bugtracker;

import com.automation.core.context.MobileContextManager;
import com.automation.enums.BugPriority;
import com.automation.enums.BugSeverity;
import com.automation.enums.BugStatus;
import com.automation.models.Bug;
import com.automation.pages.base.NativePage;
import com.automation.pages.locators.BugTrackerLocators;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Page Object for the Bug Form (Create/Edit Bug screen).
 *
 * This page handles all interactions with the bug creation/editing form.
 * It uses Native Android elements (EditText, Spinner, Button).
 *
 * Demonstrates OOP principles:
 * - Inheritance: Extends NativePage
 * - Encapsulation: Hides form interaction complexity
 * - Method overloading: Multiple ways to fill the form
 * - Fluent API: Method chaining for readable test code
 * - Single Responsibility: Manages only bug form
 *
 * @author Automation Team
 * @version 1.0
 */
@Component
@Scope("prototype")
public class BugFormPage extends NativePage {

    /**
     * Constructs a BugFormPage with dependency injection.
     *
     * @param driver         The AndroidDriver instance
     * @param waitHelper     Helper for wait operations
     * @param gestureHelper  Helper for gesture operations
     * @param contextManager Manager for context switching
     */
    @Autowired
    public BugFormPage(AndroidDriver driver,
                       WaitHelper waitHelper,
                       GestureHelper gestureHelper,
                       MobileContextManager contextManager) {
        super(driver, waitHelper, gestureHelper, contextManager);
        waitForFormToLoad();
    }

    @Override
    public boolean isLoaded() {
        ensureNativeContext();

        // Form is loaded if Bug ID field is present
        return !driver.findElements(
                AppiumBy.androidUIAutomator(
                        BugTrackerLocators.uiSelectorById(BugTrackerLocators.BUG_ID_FIELD)
                )).isEmpty();
    }

    /**
     * Waits for the form to be fully loaded.
     */
    private void waitForFormToLoad() {
        log.info("Waiting for Bug Form to load");
        sleep(1000); // Allow form to render

        try {
            waitHelper.waitForUiAutomator(
                    BugTrackerLocators.uiSelectorById(BugTrackerLocators.BUG_ID_FIELD));
            log.info("Bug Form loaded successfully");
        } catch (Exception e) {
            throw new IllegalStateException("Bug Form did not load within timeout", e);
        }
    }

    /**
     * Fills the entire bug form using a Bug model object.
     *
     * This method demonstrates the benefit of using DTOs - clean, readable API.
     *
     * @param bug The Bug model containing all form data
     * @return this page object for method chaining
     */
    public BugFormPage fillBugForm(Bug bug) {
        log.info("Filling bug form with data: {}", bug);

        if (bug.getBugId() != null) {
            setBugId(bug.getBugId());
        }

        if (bug.getDate() != null) {
            setDate(bug.getDate());
        }

        if (bug.getTitle() != null) {
            setTitle(bug.getTitle());
        }

        if (bug.getStepsToReproduce() != null) {
            setStepsToReproduce(bug.getStepsToReproduce());
        }

        if (bug.getExpectedResult() != null) {
            setExpectedResult(bug.getExpectedResult());
        }

        if (bug.getActualResult() != null) {
            setActualResult(bug.getActualResult());
        }

        if (bug.getStatus() != null) {
            selectStatus(bug.getStatus());
        }

        if (bug.getSeverity() != null) {
            selectSeverity(bug.getSeverity());
        }

        if (bug.getPriority() != null) {
            selectPriority(bug.getPriority());
        }

        if (bug.getDetectedBy() != null) {
            setDetectedBy(bug.getDetectedBy());
        }

        if (bug.getFixedBy() != null) {
            setFixedBy(bug.getFixedBy());
        }

        return this;
    }

    /**
     * Sets the Bug ID field.
     *
     * @param bugId Bug ID number
     * @return this page object for method chaining
     */
    public BugFormPage setBugId(int bugId) {
        log.debug("Setting Bug ID: {}", bugId);
        WebElement field = findFieldById(BugTrackerLocators.BUG_ID_FIELD);
        clearAndType(field, String.valueOf(bugId));
        return this;
    }

    /**
     * Sets the date field (clicks date picker).
     *
     * @param date Date value (currently just opens and closes picker)
     * @return this page object for method chaining
     */
    public BugFormPage setDate(String date) {
        log.debug("Setting date field");
        WebElement field = findFieldById(BugTrackerLocators.BUG_DATE_FIELD);

        field.click();
        sleep(1000);

        // Try to click the first available date in the calendar
        try {
            var dateElements = driver.findElements(
                    AppiumBy.androidUIAutomator(
                            "new UiSelector().clickable(true).descriptionContains(\"15\")"));

            if (dateElements.isEmpty()) {
                dateElements = driver.findElements(
                        AppiumBy.androidUIAutomator(
                                "new UiSelector().clickable(true).textMatches(\"\\\\d+\")"));
            }

            if (!dateElements.isEmpty()) {
                dateElements.get(0).click();
                sleep(500);
            }

            // Click OK button
            var okButton = driver.findElement(
                    AppiumBy.androidUIAutomator(
                            BugTrackerLocators.uiSelectorByText(BugTrackerLocators.DATE_PICKER_OK_BUTTON_HEBREW)));
            okButton.click();
            sleep(300);

        } catch (Exception e) {
            log.warn("Could not select date from calendar, pressing back", e);
            navigateBack();
            sleep(300);
        }

        return this;
    }

    /**
     * Sets the bug title.
     *
     * @param title Bug title text
     * @return this page object for method chaining
     */
    public BugFormPage setTitle(String title) {
        log.debug("Setting title: {}", title);
        WebElement field = findFieldById(BugTrackerLocators.BUG_TITLE_FIELD);
        clearAndType(field, title);
        return this;
    }

    /**
     * Sets the steps to reproduce.
     *
     * @param steps Steps to reproduce the bug
     * @return this page object for method chaining
     */
    public BugFormPage setStepsToReproduce(String steps) {
        log.debug("Setting steps to reproduce");
        WebElement field = findFieldById(BugTrackerLocators.BUG_STEPS_FIELD);
        clearAndType(field, steps);
        return this;
    }

    /**
     * Sets the expected result.
     *
     * @param expected Expected result text
     * @return this page object for method chaining
     */
    public BugFormPage setExpectedResult(String expected) {
        log.debug("Setting expected result");

        // Scroll ONCE to make both Expected Result and Actual Result visible
        scrollToField(BugTrackerLocators.BUG_EXPECTED_RESULT_FIELD);
        sleep(1000); // Wait for scroll to settle

        WebElement field = findFieldById(BugTrackerLocators.BUG_EXPECTED_RESULT_FIELD);

        // Try multiple clicks to ensure focus
        field.click();
        sleep(500);
        field.click();
        sleep(1000); // Wait for keyboard

        // Try to clear any existing text
        try {
            field.clear();
            sleep(300);
        } catch (Exception e) {
            log.debug("Could not clear field: {}", e.getMessage());
        }

        // Type using Actions
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        actions.sendKeys(expected).perform();
        sleep(500);

        log.debug("Expected result filled: {}", expected);

        // Hide keyboard
        try {
            driver.hideKeyboard();
            sleep(300);
        } catch (Exception e) {
            log.debug("Could not hide keyboard: {}", e.getMessage());
        }

        return this;
    }

    /**
     * Sets the actual result.
     *
     * @param actual Actual result text
     * @return this page object for method chaining
     */
    public BugFormPage setActualResult(String actual) {
        log.debug("Setting actual result");
        WebElement field = findFieldById(BugTrackerLocators.BUG_ACTUAL_RESULT_FIELD);
        clearAndType(field, actual);

        // Hide keyboard after filling this field so it doesn't cover Detected By field below
        try {
            driver.hideKeyboard();
            sleep(500);
            log.debug("Keyboard hidden after actual result");
        } catch (Exception e) {
            log.debug("Could not hide keyboard: {}", e.getMessage());
        }

        return this;
    }

    /**
     * Selects bug status from dropdown.
     *
     * @param status The status to select
     * @return this page object for method chaining
     */
    public BugFormPage selectStatus(BugStatus status) {
        log.debug("Selecting status: {}", status);
        selectFromDropdown(BugTrackerLocators.BUG_STATUS_FIELD, status.getDisplayName());
        return this;
    }

    /**
     * Selects bug severity from dropdown.
     *
     * @param severity The severity to select
     * @return this page object for method chaining
     */
    public BugFormPage selectSeverity(BugSeverity severity) {
        log.debug("Selecting severity: {}", severity);
        selectFromDropdown(BugTrackerLocators.BUG_SEVERITY_FIELD, severity.getDisplayName());
        return this;
    }

    /**
     * Selects bug priority from dropdown.
     *
     * @param priority The priority to select
     * @return this page object for method chaining
     */
    public BugFormPage selectPriority(BugPriority priority) {
        log.debug("Selecting priority: {}", priority);
        selectFromDropdown(BugTrackerLocators.BUG_PRIORITY_FIELD, priority.getDisplayName());
        return this;
    }

    /**
     * Sets the detected by field.
     *
     * @param detectedBy Name of person who detected the bug
     * @return this page object for method chaining
     */
    public BugFormPage setDetectedBy(String detectedBy) {
        log.debug("Setting detected by: {}", detectedBy);
        WebElement field = findFieldById(BugTrackerLocators.BUG_DETECTED_BY_FIELD);
        clearAndType(field, detectedBy);
        return this;
    }

    /**
     * Sets the fixed by field.
     *
     * @param fixedBy Name of person who fixed the bug
     * @return this page object for method chaining
     */
    public BugFormPage setFixedBy(String fixedBy) {
        log.debug("Setting fixed by: {}", fixedBy);
        WebElement field = findFieldById(BugTrackerLocators.BUG_FIXED_BY_FIELD);
        clearAndType(field, fixedBy);
        return this;
    }

    /**
     * Attaches a file to the bug (attempts to select any available file).
     *
     * @return this page object for method chaining
     */
    public BugFormPage attachFile() {
        log.info("Attempting to attach a file");

        // Scroll to attach button
        scrollToField(BugTrackerLocators.BUG_ATTACH_FILE_BUTTON);

        WebElement attachButton = findFieldById(BugTrackerLocators.BUG_ATTACH_FILE_BUTTON);
        attachButton.click();
        sleep(2000);

        // Try to select any available file from common folders
        try {
            String[] commonFolders = {"DCIM", "Download", "Documents", "Pictures"};

            for (String folder : commonFolders) {
                try {
                    var folderElement = driver.findElement(
                            AppiumBy.androidUIAutomator(
                                    "new UiSelector().textMatches(\"(?i).*" + folder + ".*\").clickable(true)"));
                    folderElement.click();
                    sleep(1000);
                    break;
                } catch (Exception ignored) {
                }
            }

            // Try to click any file
            String[] extensions = {".jpg", ".png", ".txt", ".pdf"};
            for (String ext : extensions) {
                try {
                    var fileElement = driver.findElement(
                            AppiumBy.androidUIAutomator(
                                    "new UiSelector().textContains(\"" + ext + "\").clickable(true)"));
                    fileElement.click();
                    sleep(500);
                    log.info("File attached successfully");
                    return this;
                } catch (Exception ignored) {
                }
            }

            log.warn("No suitable file found, closing file picker");
            navigateBack();
            sleep(300);

        } catch (Exception e) {
            log.error("File attachment failed", e);
            navigateBack();
            sleep(300);
        }

        return this;
    }

    /**
     * Submits the bug form by clicking the submit/add button.
     *
     * @return BugsListPage after successful submission
     */
    public BugsListPage submit() {
        log.info("Submitting bug form");

        // Always ensure native context
        ensureNativeContext();

        // Best effort: hide keyboard so the button isn't covered
        try { driver.hideKeyboard(); sleep(300); } catch (Exception ignored) {}

        // Scroll the button into view using flexible selectors
        String addBugText = BugTrackerLocators.ADD_BUG_BUTTON_TEXT; // "Add Bug"
        String uiText = BugTrackerLocators.uiSelectorByText(addBugText);
        String uiTextContains = BugTrackerLocators.uiSelectorByTextContains("Add"); // more tolerant

        try {
            // Try to bring it into view by exact text
            driver.findElement(AppiumBy.androidUIAutomator(
                    BugTrackerLocators.scrollIntoView(uiText)));
        } catch (Exception e1) {
            log.debug("Exact text scroll failed, trying textContains: {}", e1.getMessage());
            try {
                driver.findElement(AppiumBy.androidUIAutomator(
                        BugTrackerLocators.scrollIntoView(uiTextContains)));
            } catch (Exception e2) {
                log.warn("scrollIntoView text/textContains failed: {}", e2.getMessage());
            }
        }

        // Prefer a direct element->click over coordinate taps
        WebElement addBtn = null;
        try {
            // 1) Try by accessibility id (many builds expose it)
            addBtn = waitHelper.createWait(6).until(
                    org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                            AppiumBy.accessibilityId(addBugText)));
        } catch (Exception ignored) {}

        if (addBtn == null) {
            try {
                // 2) Try by exact text
                addBtn = waitHelper.createWait(6).until(
                        org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                                AppiumBy.androidUIAutomator(uiText)));
            } catch (Exception ignored) {}
        }
        if (addBtn == null) {
            try {
                // 3) Try by contains text
                addBtn = waitHelper.createWait(6).until(
                        org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                                AppiumBy.androidUIAutomator(uiTextContains)));
            } catch (Exception ignored) {}
        }
        if (addBtn == null) {
            throw new IllegalStateException("Could not locate 'Add Bug' button via a11y/text.");
        }

        // Ensure it’s clickable & enabled
        waitHelper.createWait(8).until(
                org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(addBtn));

        if (!addBtn.isEnabled()) {
            // Helpful log when the form validation keeps it disabled
            log.error("'Add Bug' button is disabled. Check required fields (Severity, Priority, Expected, etc.).");
            throw new IllegalStateException("'Add Bug' is disabled – required fields may be missing.");
        }

        addBtn.click();
        log.info("Clicked 'Add Bug'");

        // Small settle wait (submission animation)
        sleep(1500);

        return new BugsListPage(driver, waitHelper, gestureHelper, contextManager);
    }

    // --- Locators ---
    private static final By SUCCESS_MSG_ID =
            AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"statusMessage\")");
    private static final By SUCCESS_MSG_XPATH =
            By.xpath("//android.widget.TextView[@resource-id='statusMessage' and contains(@text,'Bug created successfully')]");

    // Hide keyboard helper (if you don't have one)
    private void safeHideKeyboard() {
        try { driver.hideKeyboard(); Thread.sleep(300); } catch (Exception ignored) {}
    }

    public boolean waitForSuccessMessage(int timeoutSeconds) {
        ensureNativeContext();
        safeHideKeyboard();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        try {
            // Try by resource-id
            waitHelper.createWait(timeoutSeconds)
                    .until(d -> {
                        try {
                            WebElement e = d.findElement(SUCCESS_MSG_ID);
                            return e.isDisplayed() && !e.getText().trim().isEmpty();
                        } catch (Exception ignore) { return false; }
                    });
        } catch (Exception e) {
            // Fallback: XPath
            waitHelper.createWait(timeoutSeconds)
                    .until(org.openqa.selenium.support.ui.ExpectedConditions
                            .presenceOfElementLocated(SUCCESS_MSG_XPATH));
        }

        String txt;
        try {
            txt = driver.findElement(SUCCESS_MSG_ID).getText();
        } catch (Exception ignore) {
            txt = driver.findElement(SUCCESS_MSG_XPATH).getText();
        }
        log.info("Status message after submit: {}", txt);
        return txt != null && txt.toLowerCase().contains("bug created successfully");
    }

    /** Convenience: click Add Bug, then wait for success */
    public boolean submitAndConfirm() {
        submit();                               // your existing submit() that clicks the button
        return waitForSuccessMessage(8);
    }



    // ===== Private Helper Methods =====

    /**
     * Finds a form field by its resource ID.
     *
     * @param resourceId The resource ID of the field
     * @return The WebElement representing the field
     */
    private WebElement findFieldById(String resourceId) {
        return waitHelper.waitForUiAutomator(BugTrackerLocators.uiSelectorById(resourceId));
    }

    /**
     * Scrolls to make a field visible.
     *
     * @param resourceId The resource ID of the field
     */
    private void scrollToField(String resourceId) {
        try {
            driver.findElement(
                    AppiumBy.androidUIAutomator(
                            BugTrackerLocators.scrollIntoView(
                                    BugTrackerLocators.uiSelectorById(resourceId))));
            sleep(500);
        } catch (Exception e) {
            log.debug("Could not scroll to field: {}", resourceId, e);
        }
    }

    /**
     * Clears a field and types new text.
     *
     * @param field The WebElement to type into
     * @param text  The text to type
     */
    private void clearAndType(WebElement field, String text) {
        // For WebView EditText, click and use Actions to type
        field.click();
        sleep(500);

        // Use Actions class to send keys character by character
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        actions.sendKeys(text).perform();
        sleep(300);
        log.debug("Set value using Actions: {}", text);
    }

    /**
     * Selects a value from a dropdown/spinner.
     *
     * @param fieldId The resource ID of the dropdown field
     * @param value   The value to select
     */
    private void selectFromDropdown(String fieldId, String value) {
        // Wait a bit for previous action to settle
        sleep(500);

        // Find the field directly - should be visible
        WebElement field = waitHelper.createWait(10)
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                        AppiumBy.androidUIAutomator(BugTrackerLocators.uiSelectorById(fieldId))));

        log.debug("Found field: {}", fieldId);

        field.click();
        sleep(1000); // Wait for dropdown to fully open

        try {
            // Wait for the dropdown option to be present and clickable
            // Use exact text match instead of textContains
            var option = waitHelper.createWait(10)
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                            AppiumBy.androidUIAutomator(
                                    "new UiSelector().text(\"" + value + "\")")));

            log.debug("Found dropdown option: {}", value);

            // Click the option to select it
            option.click();
            log.debug("Clicked dropdown option: {}", value);

            // Wait for dropdown to close naturally
            sleep(1500);

            log.debug("Selected dropdown value: {}", value);

        } catch (Exception e) {
            log.warn("Could not select dropdown value: {}, pressing back", value, e);
            navigateBack();
            sleep(500);
        }
    }
}