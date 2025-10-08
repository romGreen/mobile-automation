package com.automation.pages.locators;

/**
 * Centralized repository for all UI element locators in the Bug Tracker application.
 *
 * This class follows the OOP principle of encapsulation by keeping all locators
 * in one place, making them easy to maintain and update. Using constants ensures
 * type safety and prevents typos.
 *
 * Organization:
 * - Native Android locators (resource IDs)
 * - WebView locators (CSS selectors, IDs)
 * - Common text labels
 *
 * @author Automation Team
 * @version 1.0
 */
public final class BugTrackerLocators {

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static members.
     */
    private BugTrackerLocators() {
        throw new UnsupportedOperationException("Utility class - do not instantiate");
    }

    // ===== Application Package =====
    public static final String APP_PACKAGE = "com.atidcollege.bugtracker";

    // ===== Native Android Element IDs =====
    public static final String TOOLBAR_ID = APP_PACKAGE + ":id/my_toolbar";
    public static final String HOME_CONTAINER_ID = APP_PACKAGE + ":id/container";
    public static final String HOME_ACTION_ID = APP_PACKAGE + ":id/action_home";

    // ===== Bug Form Field IDs (Native) =====
    public static final String BUG_ID_FIELD = "bugId";
    public static final String BUG_DATE_FIELD = "bugDate";
    public static final String BUG_TITLE_FIELD = "bugTitle";
    public static final String BUG_STEPS_FIELD = "bugSteps";
    public static final String BUG_EXPECTED_RESULT_FIELD = "bugExpectedResult";
    public static final String BUG_ACTUAL_RESULT_FIELD = "bugActualResult";
    public static final String BUG_STATUS_FIELD = "bugStatus";
    public static final String BUG_SEVERITY_FIELD = "bugSeverity";
    public static final String BUG_PRIORITY_FIELD = "bugPriority";
    public static final String BUG_DETECTED_BY_FIELD = "bugDetectedBy";
    public static final String BUG_FIXED_BY_FIELD = "bugFixedBy";
    public static final String BUG_DATE_CLOSED_FIELD = "bugDateClosed";
    public static final String BUG_ATTACH_FILE_BUTTON = "bugFile";

    // ===== UiAutomator Selectors =====
    public static String uiSelectorById(String resourceId) {
        return String.format("new UiSelector().resourceId(\"%s\")", resourceId);
    }

    public static String uiSelectorByText(String text) {
        return String.format("new UiSelector().text(\"%s\")", text);
    }

    public static String uiSelectorByTextContains(String text) {
        return String.format("new UiSelector().textContains(\"%s\")", text);
    }

    public static String uiSelectorClickable() {
        return "new UiSelector().clickable(true)";
    }

    public static String uiScrollableSelector() {
        return "new UiSelector().scrollable(true)";
    }

    public static String scrollIntoView(String targetSelector) {
        return String.format("new UiScrollable(%s).scrollIntoView(%s)",
                uiScrollableSelector(), targetSelector);
    }

    // ===== WebView Element IDs =====
    public static final String WEB_FORM_ID = "bugForm";
    public static final String WEB_VIEW_PAGE_ID = "viewBugsPage";
    public static final String WEB_CREATE_PAGE_ID = "createBugPage";
    public static final String WEB_HOME_PAGE_ID = "homePage";
    public static final String WEB_SEARCH_INPUT = "searchInput";
    public static final String WEB_BUG_STATUS_FIELD = "bugStatus";

    // ===== Common Text Labels =====
    public static final String CREATE_BUG_TEXT = "Create Bug";
    public static final String VIEW_BUGS_TEXT = "View Bugs";
    public static final String HOME_TEXT = "Home";
    public static final String BUG_TRACKER_TITLE = "Bug Tracker Tool";
    public static final String CREATE_FORM_TITLE = "Create a Bug";
    public static final String ADD_BUG_BUTTON_TEXT = "Add Bug";
    public static final String SUBMIT_BUTTON_TEXT = "Submit";
    public static final String SAVE_BUTTON_TEXT = "Save";

    // ===== Bug Status Filter Buttons =====
    public static final String FILTER_ALL_TEXT = "All";
    public static final String FILTER_OPEN_TEXT = "Open";
    public static final String FILTER_FIXED_TEXT = "Fixed";
    public static final String FILTER_CLOSED_TEXT = "Closed";
    public static final String FILTER_NOT_A_BUG_TEXT = "Not a Bug";

    // ===== Date Picker Locators =====
    public static final String DATE_PICKER_OK_BUTTON_HEBREW = "הגדרה";
    public static final String DATE_PICKER_CANCEL_BUTTON_HEBREW = "ביטול";

    // ===== Accessibility IDs =====
    public static final String CREATE_BUG_ACCESSIBILITY_ID = "Create Bug";
    public static final String VIEW_BUGS_ACCESSIBILITY_ID = "View Bugs";
}
