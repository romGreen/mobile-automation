package com.automation.pages.locators;

/**
 * Class for all UI element locators in the app.
 * @author Rom
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
    // ===== Bug Form Field IDs =====
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

    // ===== Bugs List Page Element IDs =====
    public static final String VIEW_BUGS_PAGE_ID = "viewBugsPage";
    public static final String BUG_LIST_ID = "bugList";
    public static final String SEARCH_INPUT_ID = "searchInput";

    // ===== Edit Bug Form Field IDs =====
    public static final String EDIT_BUG_TITLE_FIELD = "editBugTitle";
    public static final String EDIT_BUG_ACTUAL_RESULT_FIELD = "editBugActualResult";
    public static final String EDIT_BUG_STATUS_FIELD = "editBugStatus";
    public static final String EDIT_BUG_SEVERITY_FIELD = "editBugSeverity";
    public static final String EDIT_BUG_PRIORITY_FIELD = "editBugPriority";
    public static final String EDIT_BUG_FIXED_BY_FIELD = "editBugFixedBy";
    public static final String EDIT_BUG_DATE_CLOSED_FIELD = "editBugDateClosed";
    public static final String EDIT_BUG_FILE_BUTTON = "editBugFile";

    // ===== Text Labels =====
    public static final String CREATE_BUG_TEXT = "Create Bug";
    public static final String VIEW_BUGS_TEXT = "View Bugs";
    public static final String HOME_TEXT = "Home";
    public static final String ADD_BUG_BUTTON_TEXT = "Add Bug";
    public static final String SAVE_CHANGES_BUTTON_TEXT = "Save Changes";
    public static final String CANCEL_EDITING_BUTTON_TEXT = "Cancel Editing";
    public static final String EDIT_BUTTON_TEXT = "Edit";
    public static final String DELETE_BUTTON_TEXT = "Delete";

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
}
