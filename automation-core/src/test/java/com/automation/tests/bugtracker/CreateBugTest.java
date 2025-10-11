package com.automation.tests.bugtracker;

import com.automation.enums.BugPriority;
import com.automation.enums.BugSeverity;
import com.automation.enums.BugStatus;
import com.automation.models.Bug;
import com.automation.pages.bugtracker.CreateBugPage;
import com.automation.pages.bugtracker.BugsListPage;
import com.automation.reporting.ExtentReportManager;
import com.automation.tests.base.BaseTest;
import com.automation.utils.DataProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Bug Creation functionality.
 *
 * This class tests the ability to create new bugs in the Bug Tracker app,
 * covering both manual data input and data-driven approaches.
 *
 * Test Coverage:
 * - Create bug with all required fields
 * - Create bug with optional fields
 * - Create bug using data-driven approach (JSON)
 * - Verify bug appears in bugs list after creation
 *
 * @author Automation Team
 * @version 1.0
 */
public class CreateBugTest extends BaseTest {

    /**
     * Test: Create a new bug with required fields.
     * I know "Fixed by" and "Date closed" should logically stay empty in creation but I wanted to test all the fields.
     *
     * Steps:
     * 1. Navigate to Create Bug form
     * 2. Fill required fields
     * 3. Submit the form
     * 4. Navigate to View Bugs and verify the bug appears in the list
     *
     * Expected Result: Bug is created successfully and appears in bugs list
     */
    @Test
    @DisplayName("Create Bug - Basic Test")
    public void testCreateBugBasic() {
        ExtentReportManager.getTest().info("Starting test: Create Bug with all required fields");

        // Step 1: Navigate to Create Bug form from anywhere
        CreateBugPage bugFormPage = getNavigationBar().goToCreateBug();

        assertThat(bugFormPage.isLoaded())
                .as("Bug form should be loaded")
                .isTrue();

        ExtentReportManager.getTest().pass("Bug form loaded successfully");

        // Step 2: Define bug ID to use
        int bugId = 123;
        ExtentReportManager.getTest().info("Using bug ID: " + bugId);

        // Step 3: Fill the bug form
        bugFormPage
                .setBugId(bugId)
                .setDate("07.08.2025")
                .setTitle("Bug Title")
                .setStepsToRecreate("1. Open app\n2. Tap button X\n3. App crashes")
                .setExpectedResult("App should not crash")
                .setActualResult("App crashes with error")
                .selectStatus(BugStatus.FIXED)
                .selectSeverity(BugSeverity.MAJOR)
                .selectPriority(BugPriority.HIGH)
                .setDetectedBy("QA")
                .setFixedBy("Alice")
                .setDateClosed("19.09.2025")
                .attachFile();

        ExtentReportManager.getTest().info("Bug form filled successfully");

        // Step 4: Submit and navigate to View Bugs to verify
        BugsListPage bugsListPage = bugFormPage.submit();

        // Step 5: Verify bug appears in the bugs list
        bugsListPage.navigationBar.goToViewBugs().waitForBugWithId(bugId);

        ExtentReportManager.getTest().pass("Bug created successfully with ID: " + bugId);
    }

    /**
     * Test: Create a bug using data from JSON file (Data-Driven Testing).
     * I know "Fixed by" and "Date closed" should logically stay empty in creation but I wanted to test all the fields.
     *
     * Steps:
     * 1. Load bug data from JSON file
     * 2. Navigate to Create Bug form
     * 3. Fill form using Bug model object
     * 4. Submit and verify
     * 5. Verify bug appears in bugs list after creation
     * Expected Result: Bug from JSON is created successfully
     */
    @Test
    @DisplayName("Create Bug - Data Driven test from JSON")
    public void testCreateBug_DataDriven() {
        ExtentReportManager.getTest().info("Starting test: Create Bug using data from JSON");

        // Step 1: Load bug data from JSON (using first bug in the file)
        Bug bugData = DataProvider.loadBugFromJson("testdata/bugs.json", 0);

        ExtentReportManager.getTest().info("Loaded bug data from JSON: " + bugData);

        // Step 2: Navigate to Create Bug form
        CreateBugPage bugFormPage = getNavigationBar().goToCreateBug();

        ExtentReportManager.getTest().pass("Navigated to Bug form");

        // Step 3: Fill form using Bug model
        bugFormPage.fillBugForm(bugData);

        ExtentReportManager.getTest().info("Bug form filled using data model");

        // Step 4: Submit and verify
        BugsListPage resultPage = bugFormPage.submit();

        // Step 5: Navigate to View Bugs tab and check the bug is in the list
        resultPage.navigationBar.goToViewBugs().waitForBugWithId(bugData.getBugId());

        ExtentReportManager.getTest().pass("Bug from JSON created successfully: " + bugData.getTitle());
    }
}
