package com.automation.enums;

/**
 * Enumeration representing the possible statuses of a bug in the bug tracking system.
 *
 * This enum encapsulates the lifecycle states of a bug from creation to closure.
 * Using enums instead of strings provides type safety and prevents invalid values.
 *
 * @author Automation Team
 * @version 1.0
 */
public enum BugStatus {
    /**
     * Bug has been reported but not yet assigned or started
     */
    OPEN("Open"),

    /**
     * Bug is currently being worked on
     */
    IN_PROGRESS("In Progress"),

    /**
     * Bug has been resolved and closed
     */
    CLOSED("Closed");

    private final String displayName;

    /**
     * Constructor for BugStatus enum.
     *
     * @param displayName The text that appears in the UI for this status
     */
    BugStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name for this status as shown in the application UI.
     *
     * @return The display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Finds a BugStatus by its display name.
     * This method is useful when parsing data from UI or external sources.
     *
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
