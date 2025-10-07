package com.automation.pages.base;

import com.automation.core.context.MobileContextManager;
import com.automation.pages.locators.BugTrackerLocators;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.android.AndroidDriver;

/**
 * Abstract base class for pages that use WebView UI elements.
 *
 * This class ensures that the driver is in WEBVIEW context when
 * interacting with page elements. It handles context switching
 * automatically.
 *
 * Demonstrates OOP principles:
 * - Inheritance: Extends BasePage
 * - Specialization: Specific behavior for WebView pages
 * - Template Method pattern: Enforces context switching
 *
 * @author Automation Team
 * @version 1.0
 */
public abstract class WebViewPage extends BasePage {

    private static final int WEBVIEW_TIMEOUT_MS = 15000;

    /**
     * Constructs a WebViewPage and ensures WebView context is active.
     *
     * @param driver         The AndroidDriver instance
     * @param waitHelper     Helper for wait operations
     * @param gestureHelper  Helper for gesture operations
     * @param contextManager Manager for context switching
     */
    protected WebViewPage(AndroidDriver driver,
                          WaitHelper waitHelper,
                          GestureHelper gestureHelper,
                          MobileContextManager contextManager) {
        super(driver, waitHelper, gestureHelper, contextManager);
        ensureWebViewContext();
    }

    /**
     * Ensures the driver is in WebView context.
     * Called automatically by constructor and can be called manually if needed.
     */
    protected void ensureWebViewContext() {
        contextManager.switchToWebView(driver, BugTrackerLocators.APP_PACKAGE, WEBVIEW_TIMEOUT_MS);
        log.debug("Switched to WebView context for page: {}", getClass().getSimpleName());
    }

    /**
     * Attempts to switch to WebView context without throwing exceptions.
     *
     * @return true if switched successfully, false otherwise
     */
    protected boolean tryEnsureWebViewContext() {
        return contextManager.trySwitchToWebView(driver, BugTrackerLocators.APP_PACKAGE, WEBVIEW_TIMEOUT_MS);
    }
}
