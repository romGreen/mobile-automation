package com.automation.exceptions;

/**
 * Exception thrown when an expected UI element cannot be found on the page.
 * @author Rom
 * @version 1.0
 */
public class ElementNotFoundException extends AutomationException {

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
    }
}
