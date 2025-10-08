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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for viewing and listing bugs.
 *
 * This class tests the ability to:
 * - View the bugs list
 * - Print/display all existing bugs
 * - Navigate between different views
 *
 * @author Automation Team
 * @version 1.0
 */
public class ViewBugsTest extends BaseTest {

    @Autowired
    private AndroidDriver driver;

    @Autowired
    private WaitHelper waitHelper;

    @Autowired
    private GestureHelper gestureHelper;

    @Autowired
    private MobileContextManager contextManager;

    /**
     * Test: Verify bugs list page loads successfully.
     *
     * Steps:
     * 1. Launch app and verify home screen
     * 2. Navigate to View Bugs tab
     * 3. Verify bugs list is displayed
     *
     * Expected Result: Bugs list page loads and displays existing bugs
     */
    @Test
    @DisplayName("View Bugs List - Verify Page Loads")
    public void testViewBugsList_PageLoads() {
        ExtentReportManager.getTest().info("Starting test: Verify Bugs List Page Loads");

        // Initialize page and verify loading
        BugsListPage bugsListPage = new BugsListPage(driver, waitHelper, gestureHelper, contextManager);

        bugsListPage.waitUntilLoaded(20000);

        assertThat(bugsListPage.isLoaded())
                .as("Bugs list page should be loaded successfully")
                .isTrue();

        ExtentReportManager.getTest().pass("Bugs list page loaded successfully");

        // Navigate to View Bugs tab
        bugsListPage.goToViewTab();

        ExtentReportManager.getTest().pass("Successfully navigated to View Bugs tab");
    }

    /**
     * Test: Print all bug titles from the app.
     *
     * This test reads and prints all bug titles from the View Bugs page
     * in the actual application.
     *
     * Steps:
     * 1. Navigate to View Bugs page
     * 2. Read all bug titles from the list
     * 3. Print/log all bug titles
     *
     * Expected Result: All bugs from the app are read and logged
     */
    @Test
    @DisplayName("Print All Bugs from App")
    public void testPrintAllBugs_FromApp() {
        ExtentReportManager.getTest().info("Starting test: Print all bugs from app");

        // Initialize page and navigate to View Bugs
        BugsListPage bugsListPage = new BugsListPage(driver, waitHelper, gestureHelper, contextManager);
        bugsListPage.waitUntilLoaded(20000);

        ExtentReportManager.getTest().info("Navigating to View Bugs page");

        // Read all bug titles from the app
        List<String> bugTitles = bugsListPage.getAllBugTitles();

        ExtentReportManager.getTest().info("Found " + bugTitles.size() + " bugs in the app");

        // Print all bug titles
        System.out.println("\n========== BUG LIST FROM APP ==========");
        System.out.println(String.format("Total Bugs: %d", bugTitles.size()));
        System.out.println("=======================================\n");

        if (bugTitles.isEmpty()) {
            System.out.println("No bugs found in the application.");
            ExtentReportManager.getTest().info("No bugs found in the application");
        } else {
            for (int i = 0; i < bugTitles.size(); i++) {
                String title = bugTitles.get(i);
                System.out.println(String.format("Bug #%d: %s", i + 1, title));
                ExtentReportManager.getTest().info("Bug #" + (i + 1) + ": " + title);
            }
        }

        System.out.println("=======================================\n");

        ExtentReportManager.getTest().pass("Successfully printed all bugs from app");
    }
}
