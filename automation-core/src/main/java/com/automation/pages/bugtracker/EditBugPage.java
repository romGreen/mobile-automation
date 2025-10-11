package com.automation.pages.bugtracker;

import com.automation.core.config.ConfigReader;
import com.automation.enums.BugPriority;
import com.automation.enums.BugSeverity;
import com.automation.enums.BugStatus;
import com.automation.pages.base.BaseFormPage;
import com.automation.pages.locators.BugTrackerLocators;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Page Object for the Edit Bug screen.
 *
 * This page handles all interactions with the edit bug form.
 * Extends BaseFormPage to inherit form-specific functionality (dropdowns, date pickers, file attachment).
 *
 * @author Rom
 * @version 1.0
 */
@Component
@Scope("prototype")
public class EditBugPage extends BaseFormPage {
    /**
     * Constructs an EditBugPage with dependency injection.
     *
     * @param driver         The AndroidDriver instance
     * @param waitHelper     Helper for wait operations
     * @param gestureHelper  Helper for gesture operations
     * @param configReader   Configuration reader for accessing settings
     */
    @Autowired
    public EditBugPage(AndroidDriver driver,
                       WaitHelper waitHelper,
                       GestureHelper gestureHelper,
                       ConfigReader configReader) {
        super(driver, waitHelper, gestureHelper, configReader);
    }

    @Override
    public boolean isLoaded() {
        // Check for fields at top and bottom to handle all cases to check if the page is loaded
        return isAnyElementPresentById(
                BugTrackerLocators.EDIT_BUG_TITLE_FIELD,
                BugTrackerLocators.EDIT_BUG_STATUS_FIELD
        ) || isElementPresentByText(BugTrackerLocators.SAVE_CHANGES_BUTTON_TEXT);
    }

    /**
     * Sets the title field.
     *
     * @param title Bug title text
     * @return this page object for method chaining
     */
    public EditBugPage setTitle(String title) {
        log.debug("Setting title: {}", title);
        ensureFieldVisible(BugTrackerLocators.EDIT_BUG_TITLE_FIELD);
        WebElement field = waitHelper.waitForUiAutomator(
                BugTrackerLocators.uiSelectorById(BugTrackerLocators.EDIT_BUG_TITLE_FIELD));
        clearAndType(field, title);
        return this;
    }

    /**
     * Sets the actual result field.
     *
     * @param actual Actual result text
     * @return this page object for method chaining
     */
    public EditBugPage setActualResult(String actual) {
        log.debug("Setting actual result: {}", actual);
        ensureFieldVisible(BugTrackerLocators.EDIT_BUG_ACTUAL_RESULT_FIELD);
        WebElement field = waitHelper.waitForUiAutomator(
                BugTrackerLocators.uiSelectorById(BugTrackerLocators.EDIT_BUG_ACTUAL_RESULT_FIELD));
        clearAndType(field, actual);

        // Hide keyboard after filling this field
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
     * Selects bug status from dropdown menu.
     *
     * @param status The status to select
     */
    public void selectStatus(BugStatus status) {
        log.debug("Selecting status: {}", status);

        // Ensure status field is visible with custom scroll actions
        ensureFieldVisible(BugTrackerLocators.EDIT_BUG_STATUS_FIELD,
            () -> {
                gestureHelper.scrollLeft();
                sleep(500);
            },
            () -> {
                gestureHelper.scrollDown();
                sleep(500);
            }
        );

        // Use generic method from BasePage with EditBugPage locator
        selectDropdownField(BugTrackerLocators.EDIT_BUG_STATUS_FIELD, status.getDisplayName());
    }

    /**
     * Selects bug severity from dropdown menu.
     *
     * @param severity The severity to select
     * @return this page object for method chaining
     */
    public EditBugPage selectSeverity(BugSeverity severity) {
        log.debug("Selecting severity: {}", severity);
        ensureFieldVisible(BugTrackerLocators.EDIT_BUG_SEVERITY_FIELD);
        selectDropdownField(BugTrackerLocators.EDIT_BUG_SEVERITY_FIELD, severity.getDisplayName());
        return this;
    }

    /**
     * Selects bug priority from dropdown menu.
     * @param priority The priority to select
     * @return this page object for method chaining
     */
    public EditBugPage selectPriority(BugPriority priority) {
        log.debug("Selecting priority: {}", priority);
        ensureFieldVisible(BugTrackerLocators.EDIT_BUG_PRIORITY_FIELD);
        selectDropdownField(BugTrackerLocators.EDIT_BUG_PRIORITY_FIELD, priority.getDisplayName());
        return this;
    }

    /**
     * Sets the fixed by field.
     * @param fixedBy Name of person who fixed the bug
     * @return this page object for method chaining
     */
    public EditBugPage setFixedBy(String fixedBy) {
        log.debug("Setting fixed by: {}", fixedBy);
        ensureFieldVisible(BugTrackerLocators.EDIT_BUG_FIXED_BY_FIELD);
        WebElement field = waitHelper.waitForUiAutomator(
                BugTrackerLocators.uiSelectorById(BugTrackerLocators.EDIT_BUG_FIXED_BY_FIELD));
        clearAndType(field, fixedBy);
        return this;
    }

    /**
     * Sets the date closed field (clicks date picker).
     * @param dateClosed Date value in format DD.MM.YYYY
     * @return this page object for method chaining
     */
    public EditBugPage setDateClosed(String dateClosed) {
        log.debug("Setting date closed field: {}", dateClosed);
        // Use generic method from BasePage with EditBugPage locator
        setDateField(BugTrackerLocators.EDIT_BUG_DATE_CLOSED_FIELD, dateClosed);
        return this;
    }

    /**
     * Attaches a specific file from test resources to the bug.
     * Pushes the file to device and then selects it from gallery.
     *
     * @param fileName Name of the file in testdata/files/ (e.g., "sample.jpg")
     * @return this page object for method chaining
     */
    public EditBugPage attachFile(String fileName) {
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
            // Use BasePage helper to push file to device
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
    public EditBugPage attachFile() {
        // Check if file attachment is enabled in config
        if (!isFileAttachmentEnabled()) {
            log.info("File attachment disabled in config, skipping");
            return this;
        }

        log.info("Attempting to attach a file using media picker");

        // Ensure attach button is visible
        ensureFieldVisible(BugTrackerLocators.EDIT_BUG_FILE_BUTTON);

        // Click attach button
        WebElement attachButton = waitHelper.waitForUiAutomator(
                BugTrackerLocators.uiSelectorById(BugTrackerLocators.EDIT_BUG_FILE_BUTTON));
        attachButton.click();
        sleep(2000);

        // Use BasePage helper to navigate media picker and select photo
        openMediaPickerAndSelectPhoto();

        return this;
    }

    /**
     * Submits the edit form by clicking "Save Changes"
     * @return BugsListPage after successful submission
     */
    public BugsListPage submit() {
        // Use generic method from BasePage
        return submitFormByButton(BugTrackerLocators.SAVE_CHANGES_BUTTON_TEXT, true);
    }
}
