package com.automation.pages.bugtracker;

import com.automation.core.config.ConfigReader;
import com.automation.pages.base.BasePage;
import com.automation.pages.locators.BugTrackerLocators;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Page Object for the Bugs List/Home screen.
 *
 * This page displays the list of bugs and provides navigation to create new bugs.
 * Uses native Android UI elements accessed via UiAutomator.
 *
 * Demonstrates OOP principles:
 * - Inheritance: Extends BasePage
 * - Encapsulation: Hides page interaction complexity
 * - Single Responsibility: Manages only bugs list page
 * - Method chaining: Fluent API for readable tests
 *
 * @author Automation Team
 * @version 1.0
 */
@Component
@Scope("prototype")
public class BugsListPage extends BasePage {

    private final ConfigReader configReader;

    /**
     * Constructs a BugsListPage with dependency injection.
     *
     * @param driver         The AndroidDriver instance
     * @param waitHelper     Helper for wait operations
     * @param gestureHelper  Helper for gesture operations
     * @param configReader   Configuration reader for accessing settings
     */
    @Autowired
    public BugsListPage(AndroidDriver driver,
                        WaitHelper waitHelper,
                        GestureHelper gestureHelper,
                        ConfigReader configReader) {
        super(driver, waitHelper, gestureHelper, configReader);
        this.configReader = configReader;
    }

    @Override
    public boolean isLoaded() {
        // Check for elements specific to the View Bugs page
        // Use helper methods from BasePage for cleaner code
        return isAnyElementPresentById(
                BugTrackerLocators.VIEW_BUGS_PAGE_ID,
                BugTrackerLocators.BUG_LIST_ID
        ) || isElementPresentByText(BugTrackerLocators.FILTER_ALL_TEXT);
    }


    /**
     * Waits for a bug with the specified Bug ID to appear in the list.
     * <p>
     * This method uses UiScrollable to automatically scroll through the bugs list
     * until the bug with the specified ID is found.
     *
     * @param bugId The bug ID to wait for
     * @throws TimeoutException if bug is not found in the entire list
     */
    public void waitForBugWithId(int bugId) {
        log.info("Waiting for bug with ID: {}", bugId);

        // Wait for the list to fully load and animations to complete
        // This prevents "Unable to perform W3C actions" errors
        sleep(2500);

        // Scroll down slightly to make sure we're not at the top
        // This prevents pull-to-refresh from triggering when UiScrollable searches
        try {
            gestureHelper.scrollDown();
            sleep(500);
        } catch (Exception e) {
            // If first scroll fails, wait a bit longer and retry once
            log.warn("First scroll failed, retrying after additional wait: {}", e.getMessage());
            sleep(1000);
            gestureHelper.scrollDown();
            sleep(500);
        }

        // Build the bug ID text pattern: "(ID: 889)"
        String bugIdText = "(ID: " + bugId + ")";

        try {
            // Use UiScrollable to automatically scroll and find the bug
            // setAsVerticalList() ensures it only scrolls down, not up
            String scrollToBug =
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".setAsVerticalList()" +  // Only scroll vertically (down)
                ".setMaxSearchSwipes(15)" +  // Allow up to 15 scrolls
                ".scrollIntoView(new UiSelector().textContains(\"" + bugIdText + "\"))";

            driver.findElement(AppiumBy.androidUIAutomator(scrollToBug));
            log.info("Bug found in list: ID {}", bugId);

        } catch (Exception e) {
            log.error("Bug not found with ID '{}' after scrolling through list", bugId);
            throw new TimeoutException("Bug not found: ID " + bugId);
        }
    }

    /**
     * Reads all bug titles from the View Bugs list.
     *
     * @return List of bug titles visible on the page
     */
    public java.util.List<String> getAllBugTitles() {
        log.info("Reading all bug titles from View Bugs page");

        try {
            // Wait for bug list container to be present
            waitHelper.createWait(10)
                    .until(ExpectedConditions.presenceOfElementLocated(
                            AppiumBy.androidUIAutomator(
                                    BugTrackerLocators.uiSelectorById(BugTrackerLocators.BUG_LIST_ID))));

            java.util.List<String> bugTitles = new java.util.ArrayList<>();
            java.util.Set<String> uniqueTitles = new java.util.HashSet<>();

            int previousSize = 0;
            int noNewBugsCount = 0;

            // Keep scrolling until no new bugs are found
            while (true) {
                // Find all TextViews that contain "(ID:" using a simpler approach
                var bugElements = driver.findElements(
                        AppiumBy.androidUIAutomator(
                                "new UiSelector().className(\"android.widget.TextView\").textContains(\"(ID:\")"));

                log.debug("Found {} elements with '(ID:' on screen", bugElements.size());

                // Add unique bug titles (extract title only, without ID)
                for (var element : bugElements) {
                    String fullText = element.getText();
                    log.debug("Element text: '{}'", fullText);

                    if (fullText != null && !fullText.trim().isEmpty()) {
                        // Check uniqueness on full text (with ID) to avoid duplicates during scrolling
                        // But add only the title part (without ID) to the result list
                        if (uniqueTitles.add(fullText)) {
                            // Extract only the title part (before "(ID: xxx)")
                            String title = fullText;
                            int idIndex = fullText.indexOf("(ID:");
                            if (idIndex > 0) {
                                title = fullText.substring(0, idIndex).trim();
                            }

                            bugTitles.add(title);
                            log.debug("Added bug title to list: {}", title);
                        }
                    }
                }
                log.debug("Current bug count: {} (previous: {})", bugTitles.size(), previousSize);

                // Check if we found new bugs
                if (bugTitles.size() == previousSize) {
                    noNewBugsCount++;
                    log.debug("No new bugs found. Count: {}", noNewBugsCount);
                    // If no new bugs found after 2 scroll attempts, we've reached the bottom
                    if (noNewBugsCount >= 2) {
                        log.info("Reached bottom of bug list");
                        break;
                    }
                } else {
                    noNewBugsCount = 0;
                    previousSize = bugTitles.size();
                    log.debug("Found new bugs. Total now: {}", bugTitles.size());
                }

                // Scroll down to see more bugs
                gestureHelper.scrollDown();
                sleep(500);
            }

            log.info("Found {} bugs in the list", bugTitles.size());
            return bugTitles;

        } catch (Exception e) {
            log.warn("No bugs found or error reading bug list: {}", e.getMessage());
            return new java.util.ArrayList<>();
        }
    }


    /**
     * Clicks the Edit button for a bug with the specified Bug ID.
     * This opens the edit form for the bug.
     *
     * Uses Bug ID to uniquely identify the correct Edit button, since multiple
     * bugs may be visible on screen with their own Edit buttons.
     *
     * @param bugId The unique Bug ID to edit
     * @return EditBugPage for editing the bug
     */
    public EditBugPage clickEditButtonForBugById(int bugId) {
        log.info("Clicking Edit button for bug ID: {}", bugId);

        try {
            // Find the bug row by looking for the unique Bug ID text
            // Bug IDs are displayed as "(ID: 888)" in the list
            String bugIdText = "(ID: " + bugId + ")";

            // Wait for bug element to be present
            var bugElement = waitHelper.createWait(10)
                    .until(ExpectedConditions.presenceOfElementLocated(
                            AppiumBy.androidUIAutomator(
                                    BugTrackerLocators.uiSelectorByTextContains(bugIdText))));

            log.info("Bug found with ID: {}", bugId);

            // Scroll right to bring the Edit button into view
            // The Edit button might be off-screen to the right if the bug title is long
            log.debug("Scrolling right to reveal Edit button");
            gestureHelper.scrollRight();
            sleep(500);

            // Now find the Edit button that's in the same row/hierarchy as this bug
            // Get all Edit buttons on screen
            var editButtons = driver.findElements(
                    AppiumBy.androidUIAutomator(
                            BugTrackerLocators.uiSelectorByText(BugTrackerLocators.EDIT_BUTTON_TEXT)));

            if (editButtons.isEmpty()) {
                throw new RuntimeException("No Edit buttons found on screen");
            }

            // Get the location of the bug ID element
            org.openqa.selenium.Rectangle bugRect = bugElement.getRect();
            int bugY = bugRect.getY();

            log.debug("Bug ID found at Y coordinate: {}", bugY);

            // Find the Edit button closest to this bug (same Y coordinate range)
            org.openqa.selenium.WebElement targetEditButton = null;
            int minDistance = Integer.MAX_VALUE;

            for (var editBtn : editButtons) {
                org.openqa.selenium.Rectangle editRect = editBtn.getRect();
                int editY = editRect.getY();

                // Calculate vertical distance between bug and edit button
                int distance = Math.abs(editY - bugY);

                log.debug("Edit button at Y: {}, distance from bug: {}", editY, distance);

                // If edit button is within 200 pixels vertically (same row)
                if (distance < 200 && distance < minDistance) {
                    targetEditButton = editBtn;
                    minDistance = distance;
                }
            }

            if (targetEditButton == null) {
                throw new RuntimeException("Could not find Edit button near bug ID: " + bugId);
            }

            log.info("Found Edit button for bug ID {} (distance: {} pixels)", bugId, minDistance);

            targetEditButton.click();
            log.info("Clicked Edit button for bug ID: {}", bugId);

            // Create EditBugPage and wait for it to load properly
            EditBugPage editPage = new EditBugPage(driver, waitHelper, gestureHelper, configReader);
            editPage.waitUntilLoaded(10000);  // Wait up to 10 seconds for edit form to load

            return editPage;

        } catch (Exception e) {
            log.error("Failed to click Edit button for bug ID: {}", bugId, e);

            // Provide helpful debugging information
            String availableElements = getAvailableClickableElements();
            log.error("Available clickable elements:\n{}", availableElements);

            throw new com.automation.exceptions.ElementNotFoundException(
                    "Edit button for bug ID: " + bugId,
                    "UiAutomator",
                    String.format("Could not find Edit button.\n%s", availableElements));
        }
    }

    /**
     * Clicks on a status filter button (All, Open, Fixed, Closed, Not a Bug).
     *
     * @param statusFilter The status filter to click (e.g., "Fixed")
     * @return this page object for method chaining
     */
    public BugsListPage clickStatusFilter(String statusFilter) {
        log.info("Clicking status filter: {}", statusFilter);

        // Only scroll up if filter is not already visible
        // Scrolling up when already at top triggers app refresh
        if (!isFilterVisible(statusFilter)) {
            log.debug("Filter not visible, scrolling up to reveal filters");
            gestureHelper.scrollUp();
            sleep(500);
        } else {
            log.debug("Filter already visible, skipping scroll");
        }

        try {
            var filterButton = waitHelper.createWait(10)
                    .until(ExpectedConditions.elementToBeClickable(
                            AppiumBy.androidUIAutomator(
                                    BugTrackerLocators.uiSelectorByText(statusFilter))));

            filterButton.click();
            log.info("Clicked status filter: {}", statusFilter);
            sleep(1500); // Wait for filtered list to load

            return this;
        } catch (Exception e) {
            log.error("Failed to click status filter: {}", statusFilter, e);
            throw new com.automation.exceptions.ElementNotFoundException(
                    "Status filter: " + statusFilter,
                    "UiAutomator",
                    "Could not find status filter. Exception: " + e.getMessage());
        }
    }

    /**
     * Checks if a status filter button is currently visible on screen.
     *
     * @param filterText The text of the filter to check
     * @return true if filter is visible, false otherwise
     */
    private boolean isFilterVisible(String filterText) {
        try {
            var elements = driver.findElements(
                    AppiumBy.androidUIAutomator(
                            BugTrackerLocators.uiSelectorByText(filterText)));

            if (elements.isEmpty()) {
                return false;
            }

            // Check if element is actually displayed (not just present in DOM)
            return elements.get(0).isDisplayed();

        } catch (Exception e) {
            log.debug("Error checking filter visibility: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verifies if a bug with the specified ID exists in the current filtered view.
     * Scrolls down if needed to find the bug.
     *
     * @param bugId The bug ID to find
     * @return true if bug is found, false otherwise
     */
    public boolean isBugInCurrentFilter(int bugId) {
        log.info("Checking if bug exists in current filter: ID {}", bugId);

        // Build the bug ID text pattern: "(ID: 895)"
        String bugIdText = "(ID: " + bugId + ")";

        try {
            // Try to find the bug without scrolling first
            if (!driver.findElements(AppiumBy.androidUIAutomator(
                    BugTrackerLocators.uiSelectorByTextContains(bugIdText))).isEmpty()) {
                log.info("Bug found in current filter (no scroll needed): ID {}", bugId);
                return true;
            }

            // Scroll down to find the bug
            for (int i = 0; i < 3; i++) {
                gestureHelper.scrollDown();
                sleep(500);

                if (!driver.findElements(AppiumBy.androidUIAutomator(
                        BugTrackerLocators.uiSelectorByTextContains(bugIdText))).isEmpty()) {
                    log.info("Bug found in current filter after scrolling: ID {}", bugId);
                    return true;
                }
            }

            log.info("Bug NOT found in current filter: ID {}", bugId);
            return false;

        } catch (Exception e) {
            log.error("Error checking for bug in filter: ID {}", bugId, e);
            return false;
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
