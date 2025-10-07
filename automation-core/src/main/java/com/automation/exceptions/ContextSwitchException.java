package com.automation.exceptions;

/**
 * Exception thrown when switching between mobile app contexts fails.
 *
 * In hybrid mobile apps, automation needs to switch between NATIVE_APP
 * and WEBVIEW contexts. This exception is thrown when such context
 * switching fails or when the expected context is not available.
 *
 * @author Automation Team
 * @version 1.0
 */
public class ContextSwitchException extends AutomationException {

    private final String targetContext;
    private final String availableContexts;

    /**
     * Constructs a ContextSwitchException with a detailed message.
     *
     * @param message The detail message
     */
    public ContextSwitchException(String message) {
        super(message);
        this.targetContext = null;
        this.availableContexts = null;
    }

    /**
     * Constructs a ContextSwitchException with message and cause.
     *
     * @param message The detail message
     * @param cause   The underlying cause
     */
    public ContextSwitchException(String message, Throwable cause) {
        super(message, cause);
        this.targetContext = null;
        this.availableContexts = null;
    }

    /**
     * Constructs a ContextSwitchException with detailed context information.
     *
     * @param targetContext      The context that was attempted to switch to
     * @param availableContexts  The contexts that are actually available
     * @param message            Additional context message
     */
    public ContextSwitchException(String targetContext, String availableContexts, String message) {
        super(String.format("Failed to switch to context '%s'. Available contexts: %s. %s",
                targetContext, availableContexts, message));
        this.targetContext = targetContext;
        this.availableContexts = availableContexts;
    }

    /**
     * Gets the target context that was attempted.
     *
     * @return Target context name, or null if not provided
     */
    public String getTargetContext() {
        return targetContext;
    }

    /**
     * Gets the available contexts at the time of failure.
     *
     * @return Available contexts string, or null if not provided
     */
    public String getAvailableContexts() {
        return availableContexts;
    }
}
