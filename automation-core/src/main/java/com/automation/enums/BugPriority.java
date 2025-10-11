package com.automation.enums;

/**
 * Enumeration representing the priority levels for bugs.
 * @author Rom
 * @version 1.0
 */
public enum BugPriority {
    CRITICAL("Critical"),
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low"),
    DEFERRED("Deferred");

    private final String displayName;

    /**
     * Constructor for BugPriority enum.
     * @param displayName The text that appears in the UI for this priority
     */
    BugPriority(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name for this priority as shown in the application UI.
     * @return The display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Finds a BugPriority by its display name.
     * @param displayName The display name to search for
     * @return The matching BugPriority, or null if not found
     */
    public static BugPriority fromDisplayName(String displayName) {
        for (BugPriority priority : values()) {
            if (priority.displayName.equalsIgnoreCase(displayName)) {
                return priority;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
