package com.automation.enums;

/**
 * Enumeration representing the priority levels for bug resolution.
 *
 * Priority indicates the urgency of fixing a bug from a business perspective.
 * Unlike severity (technical impact), priority reflects business or user impact.
 *
 * @author Automation Team
 * @version 1.0
 */
public enum BugPriority {
    /**
     * Must be fixed immediately, blocking release
     */
    CRITICAL("Critical"),

    /**
     * Should be fixed soon, high business impact
     */
    HIGH("High"),

    /**
     * Normal priority, fix in regular workflow
     */
    MEDIUM("Medium"),

    /**
     * Low priority, can be deferred
     */
    LOW("Low");

    private final String displayName;

    /**
     * Constructor for BugPriority enum.
     *
     * @param displayName The text that appears in the UI for this priority
     */
    BugPriority(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name for this priority as shown in the application UI.
     *
     * @return The display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Finds a BugPriority by its display name.
     * This method is useful when parsing data from UI or external sources.
     *
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
