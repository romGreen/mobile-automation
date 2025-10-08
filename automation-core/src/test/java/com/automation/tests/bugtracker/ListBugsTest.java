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
 * Test class for listing and viewing all bugs.
 *
 * This class tests the ability to retrieve and display all bug titles
 * from the View Bugs screen.
 *
 * Test Coverage:
 * - Retrieve all bug titles from the bugs list
 * - Print bug titles to console
 *
 * @author Automation Team
 * @version 1.0
 */
public class ListBugsTest extends BaseTest {

    @Autowired
    private AndroidDriver driver;

    @Autowired
    private WaitHelper waitHelper;

    @Autowired
    private GestureHelper gestureHelper;

    @Autowired
    private MobileContextManager contextManager;

    /**
     * Test: Retrieve and print all bug titles.
     *
     * Steps:
     * 1. Navigate to View Bugs tab
     * 2. Get all bug titles
     * 3. Print titles to console
     *
     * Expected Result: All bug titles are retrieved and displayed
     */
    @Test
    @DisplayName("List All Bug Titles")
    public void testListAllBugTitles() {
        ExtentReportManager.getTest().info("Starting test: List all bug titles");

        // Step 1: Navigate to View Bugs (only once)
        BugsListPage bugsListPage = new BugsListPage(driver, waitHelper, gestureHelper, contextManager);
        bugsListPage.waitUntilLoaded(20000);

        ExtentReportManager.getTest().info("Bugs list page loaded");

        // Step 2: Go to View Bugs tab and get all bug titles
        bugsListPage.goToViewTab();
        List<String> bugTitles = bugsListPage.getAllBugTitles();

        ExtentReportManager.getTest().info("Found " + bugTitles.size() + " bugs");

        // Step 3: Print titles
        System.out.println("\n=== All Bug Titles ===");
        for (int i = 0; i < bugTitles.size(); i++) {
            System.out.println((i + 1) + ". " + bugTitles.get(i));
        }
        System.out.println("======================\n");

        // Verify we got at least some bugs
        assertThat(bugTitles)
                .as("Bug list should not be empty")
                .isNotEmpty();

        ExtentReportManager.getTest().pass("Successfully listed " + bugTitles.size() + " bug titles");
    }
}
