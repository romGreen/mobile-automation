package com.automation.tests.bugtracker;

import com.automation.pages.bugtracker.BugsListPage;
import com.automation.reporting.ExtentReportManager;
import com.automation.tests.base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for listing and viewing all bugs.
 *
 * Test Coverage:
 * - Retrieve all bug titles from the bugs list
 * - Print bug titles to console
 *
 * @author Rom
 * @version 1.0
 */
public class ListBugsTest extends BaseTest {

    /**
     * Test: Retrieve and print all bug titles.
     *
     * Steps:
     * 1. Navigate to View Bugs tab
     * 2. Get all bug titles
     * 3. Print all the titles
     */
    @Test
    @DisplayName("List All Bug Titles")
    public void testListAllBugTitles() {
        ExtentReportManager.getTest().info("Starting test: List all bug titles");

        // Step 1: Navigate to View Bugs tab
        BugsListPage bugsListPage = getNavigationBar().goToViewBugs();

        ExtentReportManager.getTest().info("Bugs list page loaded");

        // Step 2: Get all bug titles
        List<String> bugTitles = bugsListPage.getAllBugTitles();

        ExtentReportManager.getTest().info("Found " + bugTitles.size() + " bugs");

        // Step 3: Print titles
        System.out.println("\n=== All Bug Titles ===");
        if (bugTitles.isEmpty()) {
            System.out.println("No bugs in the list");
            ExtentReportManager.getTest().pass("Bug list is empty - no bugs to display");
        } else {
            for (int i = 0; i < bugTitles.size(); i++) {
                System.out.println((i + 1) + ". " + bugTitles.get(i));
            }
            ExtentReportManager.getTest().pass("Successfully listed " + bugTitles.size() + " bug titles");
        }
        System.out.println("======================\n");
    }
}
