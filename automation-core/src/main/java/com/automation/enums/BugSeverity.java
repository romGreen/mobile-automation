package com.automation.enums;

/**
 * Enumeration representing the severity levels of a bug.
 * @author Rom
 * @version 1.0
 */
public enum BugSeverity {
    CRITICAL("Critical"),
    MAJOR("Major"),
    MINOR("Minor"),
    TRIVIAL("Trivial"),
    ENHANCEMENT("Enhancement");


    private final String displayName;

    /**
     * Constructor for BugSeverity enum.
     * @param displayName The text that appears in the UI for this severity
     */
    BugSeverity(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name for this severity as shown in the UI.
     * @return The display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Finds a BugSeverity by its display name.*
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
