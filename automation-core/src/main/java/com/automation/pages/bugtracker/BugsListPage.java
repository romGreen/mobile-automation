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
        log.info("Navigating to View Bugs tab");

        try {
            // Ensure native context
            ensureNativeContext();

            // Hide keyboard to stabilize layout
            try {
                driver.hideKeyboard();
                sleep(300);
            } catch (Exception ignored) {}

            // Scroll up to ensure View Bugs tab is visible
            gestureHelper.scrollUp();
            sleep(1000);

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
                    return this;
                }

                log.warn("Focus did not change - trying fallback tap at safe coordinates");

            } catch (Exception e) {
                log.warn("Accessibility ID click failed: {}", e.getMessage());
            }

            // Fallback: Tap at safe coordinates (right-inner area to avoid overlap)
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

            // Wait for bugs list to load
            sleep(1500);

        } catch (Exception e) {
            log.error("Failed to tap 'View Bugs' button", e);

            // Provide helpful debugging information
            String availableElements = getAvailableClickableElements();
            log.error("Available clickable elements:\n{}", availableElements);

            throw new com.automation.exceptions.ElementNotFoundException(
                    "View Bugs button",
                    "accessibilityId",
                    String.format("Button not found. Available elements:\n%s", availableElements)
            );
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

        // Use native context with UiAutomator to find bug by text
        ensureNativeContext();

        try {
            // Use UiAutomator textContains to find the bug title
            var bugElement = waitHelper.createWait(15)
                    .until(ExpectedConditions.presenceOfElementLocated(
                            AppiumBy.androidUIAutomator(
                                    BugTrackerLocators.uiSelectorByTextContains(title))));

            log.info("Bug found in list: {}", title);
            return this;
        } catch (TimeoutException e) {
            log.error("Bug not found with title: {}", title);
            throw e;
        }
    }

    /**
     * Reads all bug titles from the View Bugs list.
     *
     * @return List of bug titles visible on the page
     */
    public java.util.List<String> getAllBugTitles() {
        log.info("Reading all bug titles from View Bugs page");

        // Use native context
        ensureNativeContext();

        try {
            // Wait for bug list container to be present
            waitHelper.createWait(10)
                    .until(ExpectedConditions.presenceOfElementLocated(
                            AppiumBy.androidUIAutomator(
                                    BugTrackerLocators.uiSelectorById("bugList"))));

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

                // Add unique bug titles
                for (var element : bugElements) {
                    String title = element.getText();
                    log.debug("Element text: '{}'", title);
                    if (title != null && !title.trim().isEmpty() && uniqueTitles.add(title)) {
                        bugTitles.add(title);
                        log.debug("Added bug to list: {}", title);
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
     * Clicks on a bug with the specified title to open it for editing.
     *
     * @param bugTitle The title of the bug to click
     * @return BugFormPage for editing the bug
     */
    public BugFormPage clickBugByTitle(String bugTitle) {
        log.info("Clicking on bug with title: {}", bugTitle);

        goToViewTab();

        // Click on the bug element
        var bugElement = waitHelper.createWait(10)
                .until(ExpectedConditions.elementToBeClickable(
                        AppiumBy.xpath("//*[@id='" + BugTrackerLocators.WEB_VIEW_PAGE_ID + "']" +
                                "//*[contains(text(), '" + bugTitle + "')]")));

        bugElement.click();
        sleep(1500);

        log.info("Clicked on bug, form should open");
        return new BugFormPage(driver, waitHelper, gestureHelper, contextManager);
    }

    /**
     * Clicks the Edit button for a bug with the specified title.
     * This opens the edit form for the bug.
     *
     * @param bugTitle The title of the bug to edit
     * @return EditBugPage for editing the bug
     */
    public EditBugPage clickEditButtonForBug(String bugTitle) {
        log.info("Clicking Edit button for bug: {}", bugTitle);

        // Wait for bug list to be visible (we're already on View Bugs tab)
        sleep(2000);

        // Use native context with UiAutomator to find the Edit button
        ensureNativeContext();

        try {
            // First, verify the bug exists by finding its title
            var bugTitleElement = waitHelper.createWait(10)
                    .until(ExpectedConditions.presenceOfElementLocated(
                            AppiumBy.androidUIAutomator(
                                    BugTrackerLocators.uiSelectorByTextContains(bugTitle))));

            log.info("Bug found: {}", bugTitle);

            // Scroll horizontally to bring the Edit button into view
            // The Edit button might be off-screen to the right if title is long
            gestureHelper.scrollRight();
            sleep(500);

            // Find the Edit button using UiAutomator
            // The Edit button should be near the bug title
            var editButton = waitHelper.createWait(10)
                    .until(ExpectedConditions.elementToBeClickable(
                            AppiumBy.androidUIAutomator(
                                    BugTrackerLocators.uiSelectorByText(BugTrackerLocators.EDIT_BUTTON_TEXT))));

            editButton.click();
            log.info("Clicked Edit button for bug: {}", bugTitle);
            sleep(1500);

            return new EditBugPage(driver, waitHelper, gestureHelper, contextManager);

        } catch (Exception e) {
            log.error("Failed to click Edit button for bug: {}", bugTitle, e);

            // Provide helpful debugging information
            String availableElements = getAvailableClickableElements();
            log.error("Available clickable elements:\n{}", availableElements);

            throw new com.automation.exceptions.ElementNotFoundException(
                    "Edit button for bug: " + bugTitle,
                    "UiAutomator",
                    String.format("Could not find Edit button. Available elements:\n%s", availableElements));
        }
    }

    /**
     * Clicks on a bug card to view its details.
     *
     * @param bugTitle The title of the bug to view
     * @return this page object for method chaining
     */
    public BugsListPage clickBugCard(String bugTitle) {
        log.info("Clicking on bug card: {}", bugTitle);

        // Use native context
        ensureNativeContext();

        // Wait a moment for the list to be ready
        sleep(1000);

        try {
            // Find and click the bug title
            var bugCard = waitHelper.createWait(10)
                    .until(ExpectedConditions.elementToBeClickable(
                            AppiumBy.androidUIAutomator(
                                    BugTrackerLocators.uiSelectorByTextContains(bugTitle))));

            bugCard.click();
            log.info("Clicked on bug card: {}", bugTitle);
            sleep(1500); // Wait for details to expand

            return this;
        } catch (Exception e) {
            log.error("Failed to click bug card: {}", bugTitle, e);
            throw new com.automation.exceptions.ElementNotFoundException(
                    "Bug card: " + bugTitle,
                    "UiAutomator",
                    "Could not find bug card. Exception: " + e.getMessage());
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

        // Use native context
        ensureNativeContext();

        // Scroll up to ensure filters are visible
        gestureHelper.scrollUp();
        sleep(500);

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
     * Verifies if a bug with the specified title exists in the current filtered view.
     * Scrolls down if needed to find the bug.
     *
     * @param bugTitle The title of the bug to find
     * @return true if bug is found, false otherwise
     */
    public boolean isBugInCurrentFilter(String bugTitle) {
        log.info("Checking if bug exists in current filter: {}", bugTitle);

        // Use native context
        ensureNativeContext();

        try {
            // Try to find the bug without scrolling first
            if (!driver.findElements(AppiumBy.androidUIAutomator(
                    BugTrackerLocators.uiSelectorByTextContains(bugTitle))).isEmpty()) {
                log.info("Bug found in current filter (no scroll needed): {}", bugTitle);
                return true;
            }

            // Scroll down to find the bug
            for (int i = 0; i < 3; i++) {
                gestureHelper.scrollDown();
                sleep(500);

                if (!driver.findElements(AppiumBy.androidUIAutomator(
                        BugTrackerLocators.uiSelectorByTextContains(bugTitle))).isEmpty()) {
                    log.info("Bug found in current filter after scrolling: {}", bugTitle);
                    return true;
                }
            }

            log.info("Bug NOT found in current filter: {}", bugTitle);
            return false;

        } catch (Exception e) {
            log.error("Error checking for bug in filter: {}", bugTitle, e);
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
