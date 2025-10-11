package com.automation.enums;

/**
 * Enumeration representing the statuses of the bug.
 * @author Rom
 * @version 1.0
 */
public enum BugStatus {
    OPEN("Open"),
    FIXED("Fixed"),
    CLOSED("Closed"),
    NOT_A_BUG("Not a bug"),
    NOT_REPRODUCED("Not reproduced");

    private final String displayName;

    /**
     * Constructor for BugStatus enum.
     * @param displayName The text that appears in the UI for this status
     */
    BugStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name for this status as shown in the application UI.
     * @return The display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Finds a BugStatus by its display name.*
     * @param displayName The display name to search for
     * @return The matching BugStatus, or null if not found
     */
    public static BugStatus fromDisplayName(String displayName) {
        for (BugStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
