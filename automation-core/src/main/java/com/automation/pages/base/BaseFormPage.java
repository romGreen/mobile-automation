package com.automation.pages.base;

import com.automation.core.config.ConfigReader;
import com.automation.pages.bugtracker.BugsListPage;
import com.automation.pages.locators.BugTrackerLocators;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;

/**
 * Abstract base class for form-based Page Objects (Create and Edit pages).
 *
 * This class extends BasePage and provides additional functionality specific to forms like:
 * - Form field interactions (dropdowns, date pickers, text inputs)
 * - Form submission
 * - File attachment
 * @author Rom
 * @version 1.0
 */
public abstract class BaseFormPage extends BasePage {

    /**
     * Constructs a BaseFormPage.
     *
     * @param driver         The AndroidDriver instance
     * @param waitHelper     Helper for wait operations
     * @param gestureHelper  Helper for gesture operations
     * @param configReader   Configuration reader for accessing settings
     */
    protected BaseFormPage(AndroidDriver driver,
                           WaitHelper waitHelper,
                           GestureHelper gestureHelper,
                           ConfigReader configReader) {
        super(driver, waitHelper, gestureHelper, configReader);
    }

    // ===== Field Visibility Helper Methods =====

    /**
     * Ensures a field is visible by scrolling if necessary.
     * Checks visibility first before scrolling.
     *
     * @param fieldLocator The resource ID of the field
     * @param scrollActions Optional custom scroll actions (e.g., scrollLeft then scrollDown for EditPage)
     */
    protected void ensureFieldVisible(String fieldLocator, Runnable... scrollActions) {
        // Check if field is already visible
        if (isElementPresentById(fieldLocator)) {
            log.debug("Field '{}' already visible, skipping scroll", fieldLocator);
            return;
        }

        // Field not visible, perform scroll actions
        if (scrollActions.length > 0) {
            log.debug("Field '{}' not visible, performing custom scroll", fieldLocator);
            for (Runnable action : scrollActions) {
                action.run();
            }
        } else {
            // Default: scroll to element
            log.debug("Field '{}' not visible, scrolling to element", fieldLocator);
            gestureHelper.scrollToElement(fieldLocator);
            sleep(500);
        }
    }

    /**
     * Selects a value from a dropdown field.
     * Generic method used by both CreateBugPage and EditBugPage.
     *
     * @param fieldLocator The resource ID of the dropdown field
     * @param value The value to select from dropdown
     */
    protected void selectDropdownField(String fieldLocator, String value) {
        log.debug("Selecting dropdown field '{}': {}", fieldLocator, value);

        // Wait a bit for previous action to settle
        sleep(500);

        try {
            // Find the field
            WebElement field = waitHelper.createWait(10)
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                            AppiumBy.androidUIAutomator(BugTrackerLocators.uiSelectorById(fieldLocator))));

            log.debug("Found field: {}", fieldLocator);

            // Click field to open dropdown
            field.click();
            sleep(1000); // Wait for dropdown to open

            // Wait for the dropdown option to be present and clickable
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

    /**
     * Sets a date field by clicking it and selecting date from picker.
     * Generic method used by both CreateBugPage and EditBugPage.
     *
     * @param fieldLocator The resource ID of the date field (e.g., BugTrackerLocators.BUG_DATE_CLOSED_FIELD)
     * @param date Date value in format DD.MM.YYYY (e.g., "10.10.2025")
     */
    protected void setDateField(String fieldLocator, String date) {
        log.debug("Setting date field '{}': {}", fieldLocator, date);

        // Scroll to make date field visible
        gestureHelper.scrollToElement(fieldLocator);
        sleep(500);

        // Find and click the field
        WebElement field = waitHelper.waitForUiAutomator(BugTrackerLocators.uiSelectorById(fieldLocator));
        field.click();
        sleep(1000);

        // Parse date string (format: DD.MM.YYYY)
        try {
            String[] parts = date.split("\\.");
            if (parts.length >= 3) {
                String day = parts[0];
                int month = Integer.parseInt(parts[1]);
                String year = parts[2];

                log.debug("Parsed date - day: {}, month: {}, year: {}", day, month, year);

                // Select the date using the picker
                selectDateInPicker(day, month, year);

                // Click OK button
                var okButton = driver.findElement(
                        AppiumBy.androidUIAutomator(
                                BugTrackerLocators.uiSelectorByText(BugTrackerLocators.DATE_PICKER_OK_BUTTON_HEBREW)));
                okButton.click();
                sleep(300);
            } else {
                log.error("Invalid date format: {}, expected DD.MM.YYYY", date);
                navigateBack();
            }

        } catch (Exception e) {
            log.warn("Could not select date from calendar, pressing back", e);
            navigateBack();
            sleep(300);
        }
    }

    /**
     * Selects a specific date from the date picker by navigating to the correct month/year.
     * Uses content-desc pattern: "DD monthName YYYY" (e.g., "19 אוקטובר 2025")
     *
     * @param day Day component
     * @param month Month number
     * @param year Year
     */
    private void selectDateInPicker(String day, int month, String year) {
        log.debug("Selecting date: day={}, month={}, year={}", day, month, year);

        // Remove leading zero from day if present
        String dayWithoutZero = day.startsWith("0") ? day.substring(1) : day;

        // Get Hebrew month name
        String hebrewMonth = getHebrewMonthName(month);

        // Build content-desc pattern
        String contentDescPattern = dayWithoutZero + " " + hebrewMonth + " " + year;
        log.debug("Searching for content-desc: {}", contentDescPattern);

        // Try to find the date with exact content-desc match
        var dateElements = driver.findElements(
                AppiumBy.androidUIAutomator(
                        "new UiSelector().descriptionContains(\"" + contentDescPattern + "\")"));

        if (!dateElements.isEmpty()) {
            dateElements.get(0).click();
            sleep(500);
            log.info("Selected date successfully: {}", contentDescPattern);
            return;
        }

        // Date not found in current view - navigate to the target month/year
        log.info("Date not visible in current month, navigating to {}/{}", month, year);
        navigateToMonthYear(month, Integer.parseInt(year));

        // Try again after navigation
        dateElements = driver.findElements(
                AppiumBy.androidUIAutomator(
                        "new UiSelector().descriptionContains(\"" + contentDescPattern + "\")"));

        if (!dateElements.isEmpty()) {
            dateElements.get(0).click();
            sleep(500);
            log.info("Selected date successfully after navigation: {}", contentDescPattern);
            return;
        }

        // If still not found, try with just day and month (year might be off by one)
        String dayMonthPattern = dayWithoutZero + " " + hebrewMonth;
        log.debug("Trying with day and month only: {}", dayMonthPattern);

        dateElements = driver.findElements(
                AppiumBy.androidUIAutomator(
                        "new UiSelector().descriptionContains(\"" + dayMonthPattern + "\")"));

        if (!dateElements.isEmpty()) {
            dateElements.get(0).click();
            sleep(500);
            log.info("Selected date successfully: {}", dayMonthPattern);
            return;
        }

        // Last try: click any date with the day number
        log.warn("Could not find specific month/year, trying to select day {} only", dayWithoutZero);

        // Try with day only (text attribute)
        dateElements = driver.findElements(
                AppiumBy.androidUIAutomator(
                        "new UiSelector().clickable(true).text(\"" + dayWithoutZero + "\")"));

        if (dateElements.isEmpty() && !day.equals(dayWithoutZero)) {
            dateElements = driver.findElements(
                    AppiumBy.androidUIAutomator(
                            "new UiSelector().clickable(true).text(\"" + day + "\")"));
        }

        // Fallback: any clickable date
        if (dateElements.isEmpty()) {
            dateElements = driver.findElements(
                    AppiumBy.androidUIAutomator(
                            "new UiSelector().clickable(true).textMatches(\"\\\\d+\")"));
        }

        if (!dateElements.isEmpty()) {
            dateElements.get(0).click();
            sleep(500);
            log.info("Selected fallback date");
        } else {
            log.error("Could not find any date to select");
        }
    }

    /**
     * Navigates the date picker to the target month and year.
     *
     * @param targetMonth Target month
     * @param targetYear Target year
     */
    private void navigateToMonthYear(int targetMonth, int targetYear) {
        log.debug("Navigating to month={}, year={}", targetMonth, targetYear);

        int[] current = getCurrentMonthYearFromPicker();
        if (current == null) {
            log.warn("Could not determine current month/year, skipping navigation");
            return;
        }

        int currentMonth = current[0];
        int currentYear = current[1];

        // Calculate total months difference
        int monthsDiff = (targetYear - currentYear) * 12 + (targetMonth - currentMonth);

        log.debug("Current: month={}, year={}, Months difference: {}", currentMonth, currentYear, monthsDiff);

        if (monthsDiff == 0) {
            log.debug("Already on target month/year");
            return;
        }

        // Determine which button to click
        String buttonId = monthsDiff > 0 ? "android:id/next" : "android:id/prev";
        int clicks = Math.abs(monthsDiff);

        // Limit clicks to prevent infinite loops
        if (clicks > 120) {
            log.warn("Too many months to navigate ({}), limiting to 120", clicks);
            clicks = 120;
        }

        log.info("Clicking {} button {} times", monthsDiff > 0 ? "next" : "prev", clicks);

        for (int i = 0; i < clicks; i++) {
            try {
                var button = driver.findElement(
                        AppiumBy.androidUIAutomator(
                                "new UiSelector().resourceId(\"" + buttonId + "\")"));
                button.click();
                sleep(200);

                if ((i + 1) % 10 == 0) {
                    log.debug("Navigated {} months so far", i + 1);
                }
            } catch (Exception e) {
                log.error("Error clicking navigation button at iteration {}", i, e);
                break;
            }
        }

        sleep(500);
        log.info("Navigation complete");
    }

    /**
     * Gets the current month and year displayed in the date picker.
     * Reads from the first visible date's content-desc.
     *
     * @return Array with [month, year] or null if can't determine
     */
    private int[] getCurrentMonthYearFromPicker() {
        try {
            // Wait a bit for date picker to fully render
            sleep(500);

            // Try multiple selector strategies to find date elements
            var dateElements = driver.findElements(
                    AppiumBy.androidUIAutomator(
                            "new UiSelector().resourceId(\"android:id/month_view\").childSelector(new UiSelector().className(\"android.view.View\"))"));

            log.debug("Strategy 1 found {} date elements", dateElements.size());

            // Fallback: try without parent selector
            if (dateElements.isEmpty()) {
                dateElements = driver.findElements(
                        AppiumBy.androidUIAutomator(
                                "new UiSelector().className(\"android.view.View\").text(\"1\")"));
                log.debug("Strategy 2 (text='1') found {} elements", dateElements.size());
            }

            // Fallback: find any element with content-desc containing Hebrew month
            if (dateElements.isEmpty()) {
                dateElements = driver.findElements(
                        AppiumBy.androidUIAutomator(
                                "new UiSelector().descriptionMatches(\".* .* \\\\d{4}\")"));
                log.debug("Strategy 3 (descriptionMatches) found {} elements", dateElements.size());
            }

            log.debug("Total found {} date elements in picker", dateElements.size());

            // Try multiple elements in case first one has issues
            for (int i = 0; i < Math.min(10, dateElements.size()); i++) {
                try {
                    String contentDesc = dateElements.get(i).getAttribute("content-desc");

                    if (contentDesc == null || contentDesc.isEmpty()) {
                        log.debug("Element {} has no content-desc", i);
                        continue;
                    }

                    log.debug("Date element {} content-desc: '{}'", i, contentDesc);

                    // Split by space and expect at least 3 parts
                    String[] parts = contentDesc.split(" ");
                    if (parts.length >= 3) {
                        String hebrewMonth = parts[1];
                        String year = parts[2];

                        log.debug("Extracted from content-desc: hebrewMonth='{}', year='{}'", hebrewMonth, year);

                        // Convert Hebrew month to number
                        int monthNumber = getMonthNumberFromHebrew(hebrewMonth);

                        if (monthNumber > 0) {
                            log.info("Current picker showing: month={}, year={}", monthNumber, year);
                            return new int[]{monthNumber, Integer.parseInt(year)};
                        }
                    } else {
                        log.debug("Content-desc doesn't have 3 parts: '{}'", contentDesc);
                    }
                } catch (Exception e) {
                    log.debug("Failed to parse element {}: {}", i, e.getMessage());
                }
            }

            log.warn("Could not parse month/year from any date elements");
        } catch (Exception e) {
            log.warn("Could not determine current month/year from picker", e);
        }
        return null;
    }

    /**
     * Clears a field and types new text using Actions class.
     * Generic method for text input on any form page.
     *
     * @param field The WebElement to type into
     * @param text  The text to type
     */
    protected void clearAndType(WebElement field, String text) {
        // Click field to focus
        field.click();
        sleep(500);

        // Use Actions class to send keys character by character
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        actions.sendKeys(text).perform();
        sleep(300);
        log.debug("Set value using Actions: {}", text);
    }

    /**
     * Submits a form by clicking a button identified by text.
     * Generic method used by both CreateBugPage and EditBugPage.
     *
     * @param buttonText The text of the submit button ("Add Bug" or "Save Changes")
     * @param checkVisibilityFirst If true, only scrolls down if button is not visible
     * @return BugsListPage after successful submission
     */
    protected BugsListPage submitFormByButton(String buttonText, boolean checkVisibilityFirst) {
        log.info("Submitting form with button: {}", buttonText);

        // Hide keyboard if it's on the screen
        try {
            driver.hideKeyboard();
            sleep(300);
        } catch (Exception ignored) {}

        // Scroll down to make button visible (conditionally or always)
        if (checkVisibilityFirst) {
            // Only scroll if button is not already visible
            if (!isElementPresentByText(buttonText)) {
                log.debug("Button '{}' not visible, scrolling down", buttonText);
                gestureHelper.scrollDown();
                sleep(300);
            } else {
                log.debug("Button '{}' already visible, skipping scroll", buttonText);
            }
        } else {
            // Always scroll down
            gestureHelper.scrollDown();
            sleep(500);
        }

        try {
            // Find and click the submit button
            var submitButton = waitHelper.createWait(10)
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                            AppiumBy.androidUIAutomator(
                                    BugTrackerLocators.uiSelectorByText(buttonText))));

            submitButton.click();
            log.info("Clicked '{}' button", buttonText);

            // Wait for transition to complete
            sleep(1500);

            return new BugsListPage(driver, waitHelper, gestureHelper, configReader);

        } catch (Exception e) {
            log.error("Could not find or click '{}' button", buttonText, e);
            throw new IllegalStateException("Could not find submit button: " + buttonText, e);
        }
    }

    /**
     * Checks if file attachment is enabled in config.
     * @return true if file attachment is enabled, false otherwise
     */
    protected boolean isFileAttachmentEnabled() {
        return configReader.getBoolean("enableFileAttachment", true);
    }

    /**
     * Pushes a file from test resources to device's Camera folder.
     * Common helper method for file attachment.
     *
     * @param fileName Name of the file in testdata/files/ (e.g., "sample.jpg")
     * @throws IllegalArgumentException if file not found in resources
     */
    protected void pushFileToDevice(String fileName) {
        log.info("Attempting to push file to device: {}", fileName);

        try {
            // Read file from test resources
            String resourcePath = "testdata/files/" + fileName;
            java.io.InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);

            if (inputStream == null) {
                log.error("File not found in resources: {}", resourcePath);
                throw new IllegalArgumentException("File not found: " + resourcePath);
            }

            // Read file bytes
            byte[] fileBytes = inputStream.readAllBytes();
            inputStream.close();

            // Encode to base64 (required by Appium pushFile)
            String base64File = java.util.Base64.getEncoder().encodeToString(fileBytes);

            // Push file to device's Camera folder (where the gallery picker looks)
            String devicePath = "/sdcard/DCIM/Camera/" + fileName;
            driver.pushFile(devicePath, base64File.getBytes());

            log.info("File pushed to device Camera folder: {}", devicePath);

            // Wait for media scanner to detect the file
            sleep(2000);

        } catch (Exception e) {
            log.error("Failed to push file to device: {}", fileName, e);
            throw new RuntimeException("Failed to push file: " + fileName, e);
        }
    }

    /**
     * Navigates through the media picker and selects a photo from Camera folder.
     * Flow: Media picker → Gallery → Camera folder → Select first photo (avoiding videos because I found out the app doesn't support them)
     *
     * Common helper method for file attachment used by both CreateBugPage and EditBugPage.
     * This method assumes the attach file button has already been clicked.
     */
    protected void openMediaPickerAndSelectPhoto() {
        log.info("Navigating media picker to select photo");

        try {
            // Step 1: Click on "מדיה" / "Media"
            log.debug("Looking for Media picker option (Hebrew or English)");

            // Try Hebrew first
            var mediaPickerOption = driver.findElements(
                    AppiumBy.androidUIAutomator(
                            "new UiSelector().resourceId(\"com.android.intentresolver:id/text1\").textContains(\"מדיה\")"));

            // Fallback to English if Hebrew not found
            if (mediaPickerOption.isEmpty()) {
                log.debug("Hebrew 'מדיה' not found, trying English 'Media'");
                mediaPickerOption = driver.findElements(
                        AppiumBy.androidUIAutomator(
                                "new UiSelector().resourceId(\"com.android.intentresolver:id/text1\").textContains(\"Media\")"));
            }

            if (!mediaPickerOption.isEmpty()) {
                log.info("Found Media picker option, clicking it");
                mediaPickerOption.get(0).click();
                sleep(2000);

                // Step 2: Click on "גלריה" / "Gallery" in the upper part
                log.debug("Looking for Gallery option (Hebrew or English)");

                // Try Hebrew first
                var galleryOption = driver.findElements(
                        AppiumBy.androidUIAutomator(
                                "new UiSelector().textContains(\"גלריה\")"));

                // Fallback to English if Hebrew not found
                if (galleryOption.isEmpty()) {
                    log.debug("Hebrew 'גלריה' not found, trying English 'Gallery'");
                    galleryOption = driver.findElements(
                            AppiumBy.androidUIAutomator(
                                    "new UiSelector().textContains(\"Gallery\")"));
                }

                if (!galleryOption.isEmpty()) {
                    log.info("Found Gallery option, clicking it");
                    galleryOption.get(0).click();
                    sleep(1500);

                    // Step 3: Click on "מצלמה" / "Camera" folder
                    log.debug("Looking for Camera folder (Hebrew or English)");

                    // Try Hebrew first
                    var cameraFolder = driver.findElements(
                            AppiumBy.androidUIAutomator(
                                    "new UiSelector().textContains(\"מצלמה\")"));

                    // Fallback to English if Hebrew not found
                    if (cameraFolder.isEmpty()) {
                        log.debug("Hebrew 'מצלמה' not found, trying English 'Camera'");
                        cameraFolder = driver.findElements(
                                AppiumBy.androidUIAutomator(
                                        "new UiSelector().textContains(\"Camera\")"));
                    }

                    if (!cameraFolder.isEmpty()) {
                        log.info("Found Camera folder, clicking it");
                        cameraFolder.get(0).click();
                        sleep(1500);

                        // Step 4: Select the first photo (not video)
                        log.debug("Looking for first photo to select (avoiding videos)");

                        // Samsung Gallery uses FrameLayout items with recycler_view_item resource-id
                        var photos = driver.findElements(
                                AppiumBy.androidUIAutomator(
                                        "new UiSelector().resourceId(\"com.sec.android.gallery3d:id/recycler_view_item\")"));

                        log.debug("Found {} potential photo/video elements", photos.size());

                        boolean photoSelected = false;

                        if (!photos.isEmpty()) {
                            // IDs that indicate a VIDEO tile
                            final String DURATION_ID = "com.sec.android.gallery3d:id/content_duration";
                            final String TYPE_ICON_ID = "com.sec.android.gallery3d:id/content_type_icon";

                            for (int i = 0; i < photos.size(); i++) {
                                var tile = photos.get(i);

                                boolean looksLikeVideo =
                                        !tile.findElements(AppiumBy.id(DURATION_ID)).isEmpty() ||
                                                !tile.findElements(AppiumBy.id(TYPE_ICON_ID)).isEmpty();

                                // Some builds put duration in content-desc; skip if matches mm:ss
                                if (!looksLikeVideo) {
                                    try {
                                        String desc = tile.getAttribute("content-desc");
                                        if (desc != null && desc.matches(".*\\d+:\\d{2}.*")) {
                                            looksLikeVideo = true;
                                        }
                                    } catch (Exception ignore) {}
                                }

                                if (looksLikeVideo) {
                                    log.debug("Tile {} detected as VIDEO! Skipping it", i);
                                    continue;
                                }

                                // This tile looks like an IMAGE
                                try {
                                    log.info("Selecting tile {} as IMAGE", i);
                                    tile.click();
                                    sleep(1500);
                                    photoSelected = true;
                                    log.info("Photo selected and attached successfully");
                                    break;
                                } catch (Exception e) {
                                    log.warn("Failed clicking tile {}: {}", i, e.getMessage());
                                }
                            }
                        }

                        if (!photoSelected) {
                            log.warn("No photos found or selection failed, going back to form");
                            // Need to go back twice: once from Camera folder to Gallery, once from Gallery to form
                            navigateBack();
                            sleep(500);
                            navigateBack();
                            sleep(500);
                        }

                    } else {
                        log.warn("Camera folder not found, closing picker");
                        navigateBack();
                        sleep(500);
                    }

                } else {
                    log.warn("Gallery option not found, closing picker");
                    navigateBack();
                    sleep(500);
                }

            } else {
                log.warn("Media picker option not found in intent chooser, closing picker");
                navigateBack();
                sleep(300);
            }

        } catch (Exception e) {
            log.error("File attachment failed: {}", e.getMessage(), e);
            try {
                navigateBack();
                sleep(500);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Finds an element by its resource ID.
     * Helper method for form pages.
     *
     * @param resourceId The resource ID
     * @return The WebElement
     */
    protected WebElement findFieldById(String resourceId) {
        return waitHelper.waitForUiAutomator(BugTrackerLocators.uiSelectorById(resourceId));
    }
}
