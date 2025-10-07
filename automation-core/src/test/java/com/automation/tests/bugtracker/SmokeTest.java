package com.automation.tests.bugtracker;

import com.automation.core.context.MobileContextManager;
import com.automation.pages.bugtracker.BugsListPage;
import com.automation.reporting.ExtentReportManager;
import com.automation.tests.base.BaseTest;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke test suite for basic app functionality.
 *
 * Smoke tests verify that the most critical features work correctly
 * after deployment. These tests should be:
 * - Fast to execute
 * - Cover core functionality
 * - Run before detailed test suites
 *
 * @author Automation Team
 * @version 1.0
 */
public class SmokeTest extends BaseTest {

    @Autowired
    private AndroidDriver driver;

    @Autowired
    private WaitHelper waitHelper;

    @Autowired
    private GestureHelper gestureHelper;

    @Autowired
    private MobileContextManager contextManager;

    /**
     * Test: Verify app launches successfully.
     *
     * Steps:
     * 1. Launch the app
     * 2. Verify home screen appears
     * 3. Check that WebView context is available
     *
     * Expected Result: App launches without crashes
     */
    @Test
    @DisplayName("Smoke - App Launches Successfully")
    public void testAppLaunchesSuccessfully() {
        ExtentReportManager.getTest().info("Starting smoke test: App Launch");

        BugsListPage bugsListPage = new BugsListPage(driver, waitHelper, gestureHelper, contextManager);

        // Verify page loads within timeout
        bugsListPage.waitUntilLoaded(20000);

        assertThat(bugsListPage.isLoaded())
                .as("App should launch and home screen should be visible")
                .isTrue();

        ExtentReportManager.getTest().pass("App launched successfully");

        // Verify driver is active
        assertThat(driver.getSessionId())
                .as("Driver session should be active")
                .isNotNull();

        ExtentReportManager.getTest().pass("Driver session is active");

        // Verify contexts are available
        var contexts = driver.getContextHandles();

        assertThat(contexts)
                .as("At least NATIVE_APP context should be available")
                .isNotEmpty()
                .contains("NATIVE_APP");

        ExtentReportManager.getTest().info("Available contexts: " + contexts);
        ExtentReportManager.getTest().pass("Smoke test passed - App is functional");
    }

    /**
     * Test: Verify navigation to Create Bug screen works.
     *
     * Steps:
     * 1. Launch app
     * 2. Tap "Create Bug" button
     * 3. Verify form loads
     *
     * Expected Result: Create Bug form is accessible
     */
    @Test
    @DisplayName("Smoke - Navigation to Create Bug Works")
    public void testNavigationToCreateBug() {
        ExtentReportManager.getTest().info("Starting smoke test: Navigation to Create Bug");

        BugsListPage bugsListPage = new BugsListPage(driver, waitHelper, gestureHelper, contextManager);
        bugsListPage.waitUntilLoaded(20000);

        // Tap Create Bug
        var bugFormPage = bugsListPage.tapCreateBug();

        assertThat(bugFormPage.isLoaded())
                .as("Bug creation form should load")
                .isTrue();

        ExtentReportManager.getTest().pass("Successfully navigated to Create Bug form");
    }
}
