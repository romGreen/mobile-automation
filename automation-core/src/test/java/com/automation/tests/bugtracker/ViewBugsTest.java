package com.automation.tests.bugtracker;

import com.automation.core.context.MobileContextManager;
import com.automation.pages.bugtracker.BugsListPage;
import com.automation.reporting.ExtentReportManager;
import com.automation.tests.base.BaseTest;
import com.automation.utils.DataProvider;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import com.automation.models.Bug;
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
     * Test: Print all bug titles from test data.
     *
     * This test demonstrates reading bug data from JSON and logging it,
     * simulating the requirement to "print list of all existing bugs".
     *
     * Steps:
     * 1. Load bug data from JSON file
     * 2. Print/log all bug titles and IDs
     * 3. Verify data was loaded correctly
     *
     * Expected Result: All bugs from JSON are loaded and logged
     */
    @Test
    @DisplayName("Print All Bugs from Test Data")
    public void testPrintAllBugs_FromTestData() {
        ExtentReportManager.getTest().info("Starting test: Print all bugs from test data");

        // Load all bugs from JSON
        List<Bug> bugs = DataProvider.loadBugsFromJson("testdata/bugs.json");

        assertThat(bugs)
                .as("Bug list should not be empty")
                .isNotEmpty();

        ExtentReportManager.getTest().info("Loaded " + bugs.size() + " bugs from JSON");

        // Print all bug details
        System.out.println("\n========== BUG LIST ==========");
        System.out.println(String.format("Total Bugs: %d", bugs.size()));
        System.out.println("==============================\n");

        for (int i = 0; i < bugs.size(); i++) {
            Bug bug = bugs.get(i);

            String bugInfo = String.format(
                    "Bug #%d:\n" +
                    "  ID: %d\n" +
                    "  Title: %s\n" +
                    "  Status: %s\n" +
                    "  Severity: %s\n" +
                    "  Priority: %s\n" +
                    "  Detected By: %s\n" +
                    "  ---",
                    i + 1,
                    bug.getBugId(),
                    bug.getTitle(),
                    bug.getStatus(),
                    bug.getSeverity(),
                    bug.getPriority(),
                    bug.getDetectedBy()
            );

            System.out.println(bugInfo);

            ExtentReportManager.getTest().info("Bug #" + (i + 1) + ": " + bug.getTitle() +
                    " (ID: " + bug.getBugId() + ", Status: " + bug.getStatus() + ")");
        }

        System.out.println("==============================\n");

        ExtentReportManager.getTest().pass("Successfully printed all " + bugs.size() + " bugs");
    }
}
