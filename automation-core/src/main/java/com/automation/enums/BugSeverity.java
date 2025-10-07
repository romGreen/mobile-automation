package com.automation.enums;

/**
 * Enumeration representing the severity levels of a bug.
 *
 * Severity indicates the impact of the bug on the system's functionality.
 * This helps prioritize bug fixes based on their technical impact.
 *
 * @author Automation Team
 * @version 1.0
 */
public enum BugSeverity {
    /**
     * System crash, data loss, or complete feature failure
     */
    CRITICAL("Critical"),

    /**
     * Major functionality is severely impaired
     */
    MAJOR("Major"),

    /**
     * Feature works but with significant issues
     */
    MINOR("Minor"),

    /**
     * Cosmetic issues or minor inconveniences
     */
    TRIVIAL("Trivial");

    private final String displayName;

    /**
     * Constructor for BugSeverity enum.
     *
     * @param displayName The text that appears in the UI for this severity
     */
    BugSeverity(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name for this severity as shown in the application UI.
     *
     * @return The display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Finds a BugSeverity by its display name.
     * This method is useful when parsing data from UI or external sources.
     *
     * @param displayName The display name to search for
     * @return The matching BugSeverity, or null if not found
     */
    public static BugSeverity fromDisplayName(String displayName) {
        for (BugSeverity severity : values()) {
            if (severity.displayName.equalsIgnoreCase(displayName)) {
                return severity;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
