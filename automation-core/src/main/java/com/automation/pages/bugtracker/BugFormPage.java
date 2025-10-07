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
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
        WebElement field = findFieldById(BugTrackerLocators.BUG_EXPECTED_RESULT_FIELD);
        clearAndType(field, expected);
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

        // Scroll to ensure field is visible
        scrollToField(BugTrackerLocators.BUG_DETECTED_BY_FIELD);

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

        // Scroll to bottom where submit button typically is
        try {
            driver.findElement(
                    AppiumBy.androidUIAutomator(
                            BugTrackerLocators.scrollIntoView(
                                    BugTrackerLocators.uiSelectorByText(BugTrackerLocators.ADD_BUG_BUTTON_TEXT))));
            sleep(500);
        } catch (Exception ignored) {
        }

        // Try multiple button text variations
        String[] buttonTexts = {"Add Bug", "Submit", "Save", "Create", "שמור", "הוסף"};

        for (String buttonText : buttonTexts) {
            try {
                var button = driver.findElement(
                        AppiumBy.androidUIAutomator(
                                "new UiSelector().textMatches(\"(?i)" + buttonText + "\")"));
                button.click();
                log.info("Clicked submit button: {}", buttonText);
                sleep(1000);

                return new BugsListPage(driver, waitHelper, gestureHelper, contextManager);
            } catch (Exception ignored) {
            }
        }

        throw new IllegalStateException(
                "Could not find submit button. Tried: " + String.join(", ", buttonTexts));
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
        // For Android EditText, use setValue which is more reliable than sendKeys
        try {
            field.click();
            sleep(200);

            // Use setValue() which internally uses ACTION_SET_TEXT
            ((io.appium.java_client.android.AndroidElement) field).setValue(text);
            sleep(200);
            log.debug("Successfully set value using setValue()");

        } catch (ClassCastException e) {
            log.debug("Element is not AndroidElement, using sendKeys");

            // Fallback to sendKeys
            field.click();
            sleep(300);

            try {
                field.clear();
            } catch (Exception ex) {
                log.debug("Clear failed: {}", ex.getMessage());
            }

            field.sendKeys(text);
            sleep(200);
        }
    }

    /**
     * Selects a value from a dropdown/spinner.
     *
     * @param fieldId The resource ID of the dropdown field
     * @param value   The value to select
     */
    private void selectFromDropdown(String fieldId, String value) {
        WebElement field = findFieldById(fieldId);
        field.click();
        sleep(800);

        try {
            var option = driver.findElement(
                    AppiumBy.androidUIAutomator(
                            "new UiSelector().textContains(\"" + value + "\")"));
            option.click();
            sleep(300);
            log.debug("Selected dropdown value: {}", value);
        } catch (Exception e) {
            log.warn("Could not select dropdown value: {}", value, e);
            navigateBack();
            sleep(300);
        }
    }
}
