package com.automation.pages.bugtracker;

import com.automation.core.config.ConfigReader;
import com.automation.pages.base.BasePage;
import com.automation.pages.locators.BugTrackerLocators;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Page Object for the Home screen.
 * The home screen displays a welcome message and serves as the main entry point
 * of the application. Navigation to other screens is done
 * by the navigation bar at the top of the screen.
 * @author Rom
 * @version 1.0
 */
@Component
@Scope("prototype")
public class HomePage extends BasePage {
    /**
     * Constructs a HomePage with dependency injection.
     *
     * @param driver         The AndroidDriver instance
     * @param waitHelper     Helper for wait operations
     * @param gestureHelper  Helper for gesture operations
     * @param configReader   Configuration reader for accessing settings
     */
    @Autowired
    public HomePage(AndroidDriver driver,
                    WaitHelper waitHelper,
                    GestureHelper gestureHelper,
                    ConfigReader configReader) {
        super(driver, waitHelper, gestureHelper, configReader);
    }

    @Override
    public boolean isLoaded() {
        // Check for Welcome text - unique to Home page
        boolean hasWelcomeText = !driver.findElements(
                AppiumBy.androidUIAutomator(
                        BugTrackerLocators.uiSelectorByTextContains("Welcome"))).isEmpty();

        return hasWelcomeText;
    }
}
