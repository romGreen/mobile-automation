package com.automation.pages.base;

import com.automation.core.context.MobileContextManager;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.android.AndroidDriver;

/**
 * Abstract base class for pages that use Native Android UI elements.
 *
 * This class ensures that the driver is always in NATIVE_APP context
 * when interacting with page elements.
 *
 * Demonstrates OOP principles:
 * - Inheritance: Extends BasePage
 * - Specialization: Specific behavior for native pages
 * - Template Method pattern: Enforces context switching
 *
 * @author Automation Team
 * @version 1.0
 */
public abstract class NativePage extends BasePage {

    /**
     * Constructs a NativePage and ensures native context is active.
     *
     * @param driver         The AndroidDriver instance
     * @param waitHelper     Helper for wait operations
     * @param gestureHelper  Helper for gesture operations
     * @param contextManager Manager for context switching
     */
    protected NativePage(AndroidDriver driver,
                         WaitHelper waitHelper,
                         GestureHelper gestureHelper,
                         MobileContextManager contextManager) {
        super(driver, waitHelper, gestureHelper, contextManager);
        ensureNativeContext();
    }

    /**
     * Ensures the driver is in native context.
     * Called automatically by constructor and can be called manually if needed.
     */
    protected void ensureNativeContext() {
        contextManager.switchToNative(driver);
        log.debug("Switched to native context for page: {}", getClass().getSimpleName());
    }
}
