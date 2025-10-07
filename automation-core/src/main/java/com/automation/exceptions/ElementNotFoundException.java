package com.automation.exceptions;

/**
 * Exception thrown when an expected UI element cannot be found on the page.
 *
 * This exception is thrown when element location strategies fail to find
 * the target element within the specified timeout period. It provides
 * detailed information about what was being searched for and why it failed.
 *
 * Use this instead of generic NoSuchElementException for better error clarity.
 *
 * @author Automation Team
 * @version 1.0
 */
public class ElementNotFoundException extends AutomationException {

    private final String elementDescription;
    private final String locatorStrategy;

    /**
     * Constructs an ElementNotFoundException with a detailed message.
     *
     * @param message The detail message
     */
    public ElementNotFoundException(String message) {
        super(message);
        this.elementDescription = null;
        this.locatorStrategy = null;
    }

    /**
     * Constructs an ElementNotFoundException with message and cause.
     *
     * @param message The detail message
     * @param cause   The underlying cause
     */
    public ElementNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.elementDescription = null;
        this.locatorStrategy = null;
    }

    /**
     * Constructs an ElementNotFoundException with detailed element information.
     *
     * @param elementDescription Human-readable description of the element
     * @param locatorStrategy    The locator strategy used (e.g., "id", "xpath")
     * @param message            Additional context message
     */
    public ElementNotFoundException(String elementDescription, String locatorStrategy, String message) {
        super(String.format("Element not found: %s (using %s). %s",
                elementDescription, locatorStrategy, message));
        this.elementDescription = elementDescription;
        this.locatorStrategy = locatorStrategy;
    }

    /**
     * Gets the description of the element that was not found.
     *
     * @return Element description, or null if not provided
     */
    public String getElementDescription() {
        return elementDescription;
    }

    /**
     * Gets the locator strategy that was used to find the element.
     *
     * @return Locator strategy, or null if not provided
     */
    public String getLocatorStrategy() {
        return locatorStrategy;
    }
}
