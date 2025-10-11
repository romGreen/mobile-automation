package com.automation.pages.bugtracker;


import com.automation.core.config.ConfigReader;
import com.automation.enums.BugPriority;
import com.automation.enums.BugSeverity;
import com.automation.enums.BugStatus;
import com.automation.models.Bug;
import com.automation.pages.base.BaseFormPage;
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

/**
 * Page Object for the Create Bug Form screen.
 *
 * This page handles all interactions with the bug creation form.
 * Uses native Android UI elements (EditText, Spinner, Button) accessed via UiAutomator.
 * Extends BaseFormPage to inherit form-specific functionality (dropdowns, date pickers, file attachment).
 *
 * @author Rom
 * @version 1.0
 */
@Component
@Scope("prototype")
public class CreateBugPage extends BaseFormPage {

    /**
     * Constructs a CreateBugPage with dependency injection.
     *
     * @param driver         The AndroidDriver instance
     * @param waitHelper     Helper for wait operations
     * @param gestureHelper  Helper for gesture operations
     * @param configReader   Configuration reader for accessing settings
     */
    @Autowired
    public CreateBugPage(AndroidDriver driver,
                         WaitHelper waitHelper,
                         GestureHelper gestureHelper,
                         ConfigReader configReader) {
        super(driver, waitHelper, gestureHelper, configReader);
    }

    @Override
    public boolean isLoaded() {
        // Form is loaded if Bug ID field is present
        return isElementPresentById(BugTrackerLocators.BUG_ID_FIELD);
    }

    /**
     * Fills the entire bug form using a Bug model object.
     * <p>
     * This method demonstrates the benefit of using DTOs - clean, readable API.
     *
     * @param bug The Bug model containing all form data
     */
    public void fillBugForm(Bug bug) {
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
            setStepsToRecreate(bug.getStepsToReproduce());
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

        if (bug.getDateClosed() != null) {
            setDateClosed(bug.getDateClosed());
        }

        if (bug.getAttachedFile() != null) {
            attachFile(bug.getAttachedFile());
        }
    }

    /**
     * Sets the Bug ID field.
     * @param bugId Bug ID number
     * @return this page object for method chaining
     */
    public CreateBugPage setBugId(int bugId) {
        log.debug("Setting Bug ID: {}", bugId);
        WebElement field = findFieldById(BugTrackerLocators.BUG_ID_FIELD);
        clearAndType(field, String.valueOf(bugId));
        return this;
    }

    /**
     * Sets the date field (clicks date picker).
     * @param date Date value in format DD.MM.YYYY
     * @return this page object for method chaining
     */
    public CreateBugPage setDate(String date) {
        log.debug("Setting date field: {}", date);
        // Use method from BaseFormPage
        setDateField(BugTrackerLocators.BUG_DATE_FIELD, date);
        return this;
    }

    /**
     * Sets the bug title.
     * @param title Bug title text
     * @return this page object for method chaining
     */
    public CreateBugPage setTitle(String title) {
        log.debug("Setting title: {}", title);
        WebElement field = findFieldById(BugTrackerLocators.BUG_TITLE_FIELD);
        clearAndType(field, title);
        return this;
    }

    /**
     * Sets the steps to reproduce.
     * @param steps Steps to reproduce the bug
     * @return this page object for method chaining
     */
    public CreateBugPage setStepsToRecreate(String steps) {
        log.debug("Setting steps to reproduce");
        WebElement field = findFieldById(BugTrackerLocators.BUG_STEPS_FIELD);
        clearAndType(field, steps);
        return this;
    }

    /**
     * Sets the expected result.
     * @param expected Expected result text
     * @return this page object for method chaining
     */
    public CreateBugPage setExpectedResult(String expected) {
        log.debug("Setting expected result");

        // Scroll to make "Expected Result" visible
        gestureHelper.scrollToElement(BugTrackerLocators.BUG_EXPECTED_RESULT_FIELD);
        sleep(1000);

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
    public CreateBugPage setActualResult(String actual) {
        log.debug("Setting actual result");
        WebElement field = findFieldById(BugTrackerLocators.BUG_ACTUAL_RESULT_FIELD);
        clearAndType(field, actual);

        // Hide keyboard after filling this field so it doesn't cover fields below
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
     */
    public CreateBugPage selectStatus(BugStatus status) {
        log.debug("Selecting status: {}", status);
        // Use method from BaseFormPage
        selectDropdownField(BugTrackerLocators.BUG_STATUS_FIELD, status.getDisplayName());
        return this;
    }

    /**
     * Selects bug severity from dropdown.
     *
     * @param severity The severity to select
     */
    public CreateBugPage selectSeverity(BugSeverity severity) {
        log.debug("Selecting severity: {}", severity);
        // Use method from BaseFormPage
        selectDropdownField(BugTrackerLocators.BUG_SEVERITY_FIELD, severity.getDisplayName());
        return this;
    }

    /**
     * Selects bug priority from dropdown.
     *
     * @param priority The priority to select
     */
    public CreateBugPage selectPriority(BugPriority priority) {
        log.debug("Selecting priority: {}", priority);
        // Use method from BaseFormPage
        selectDropdownField(BugTrackerLocators.BUG_PRIORITY_FIELD, priority.getDisplayName());
        return this;
    }

    /**
     * Sets the detected by field.
     *
     * @param detectedBy Name of person who detected the bug
     * @return this page object for method chaining
     */
    public CreateBugPage setDetectedBy(String detectedBy) {
        log.debug("Setting detected by: {}", detectedBy);
        WebElement field = findFieldById(BugTrackerLocators.BUG_DETECTED_BY_FIELD);
        clearAndType(field, detectedBy);
        return this;
    }

    /**
     * Sets the fixed by field.
     *
     * @param fixedBy Name of person who fixed the bug
     */
    public CreateBugPage setFixedBy(String fixedBy) {
        log.debug("Setting fixed by: {}", fixedBy);
        WebElement field = findFieldById(BugTrackerLocators.BUG_FIXED_BY_FIELD);
        clearAndType(field, fixedBy);
        return this;
    }

    /**
     * Sets the date closed field (clicks date picker).
     *
     * @param dateClosed Date value in format DD.MM.YYYY
     * @return this page object for method chaining
     */
    public CreateBugPage setDateClosed(String dateClosed) {
        log.debug("Setting date closed field: {}", dateClosed);
        // Use method from BaseFormPage
        setDateField(BugTrackerLocators.BUG_DATE_CLOSED_FIELD, dateClosed);
        return this;
    }

    /**
     * Attaches a specific file from test resources to the bug.
     * Pushes the file to device and then selects it from gallery.
     *
     * @param fileName Name of the file in testdata/files/ (e.g., "sample.jpg")
     * @return this page object for method chaining
     */
    public CreateBugPage attachFile(String fileName) {
        // Check if file attachment is enabled in config
        if (!isFileAttachmentEnabled()) {
            log.info("File attachment disabled in config, skipping");
            return this;
        }

        if (fileName == null || fileName.isEmpty()) {
            log.warn("File name is null or empty, skipping attachment");
            return this;
        }

        try {
            // Use BaseFormPage helper to push file to device
            pushFileToDevice(fileName);

            // Now select it from gallery
            attachFile();

        } catch (Exception e) {
            log.error("Failed to attach file from resources: {}", fileName, e);
        }

        return this;
    }

    /**
     * Attaches a file to the bug by selecting from media picker.
     * Flow: Media picker → Gallery → Camera folder → Select first photo
     *
     * If file attachment is disabled in config (enableFileAttachment=false),
     * this method will skip the attachment and return immediately.
     *
     * @return this page object for method chaining
     */
    public CreateBugPage attachFile() {
        // Check if file attachment is enabled in config
        if (!isFileAttachmentEnabled()) {
            log.info("File attachment disabled in config, skipping");
            return this;
        }

        log.info("Attempting to attach a file using media picker");

        // Scroll to attach button
        gestureHelper.scrollToElement(BugTrackerLocators.BUG_ATTACH_FILE_BUTTON);
        sleep(500);

        // Click attach button
        WebElement attachButton = findFieldById(BugTrackerLocators.BUG_ATTACH_FILE_BUTTON);
        attachButton.click();
        sleep(2000);

        // Use BaseFormPage helper to navigate media picker and select photo
        openMediaPickerAndSelectPhoto();

        return this;
    }

    /**
     * Submits the bug form by clicking the add button.
     * @return BugsListPage after successful submission
     */
    public BugsListPage submit() {
        // Use method from BaseFormPage with visibility check enabled
        return submitFormByButton(BugTrackerLocators.ADD_BUG_BUTTON_TEXT, true);
    }

    /**
     * Waits for success message after bug creation.
     * @param timeoutSeconds Maximum time to wait in seconds
     * @return true if success message appears with correct text
     */
    public boolean waitForSuccessMessage(int timeoutSeconds) {
        gestureHelper.scrollDown();

        try {
            // Wait for success message by resource ID
            By successMsgLocator = AppiumBy.androidUIAutomator(
                    BugTrackerLocators.uiSelectorById("statusMessage"));
            WebElement msgElement = waitHelper.createWait(timeoutSeconds)
                    .until(ExpectedConditions.presenceOfElementLocated(successMsgLocator));

            String text = msgElement.getText();
            log.info("Status message after submit: {}", text);
            return text != null && text.toLowerCase().contains("bug created successfully");

        } catch (Exception e) {
            log.error("Success message not found within {} seconds", timeoutSeconds, e);
            return false;
        }
    }

    /**
     * Submits the form and waits for success message.
     * Convenience method combining submit() + waitForSuccessMessage().
     *
     * @return true if success message appears
     */
    public boolean submitAndConfirm() {
        submit();
        return waitForSuccessMessage(5);
    }
}
