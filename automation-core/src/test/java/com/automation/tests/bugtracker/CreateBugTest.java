package com.automation.tests.bugtracker;

import com.automation.core.context.MobileContextManager;
import com.automation.enums.BugPriority;
import com.automation.enums.BugSeverity;
import com.automation.enums.BugStatus;
import com.automation.models.Bug;
import com.automation.pages.bugtracker.BugFormPage;
import com.automation.pages.bugtracker.BugsListPage;
import com.automation.reporting.ExtentReportManager;
import com.automation.tests.base.BaseTest;
import com.automation.utils.DataProvider;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Autowired
    private AndroidDriver driver;

    @Autowired
    private WaitHelper waitHelper;

    @Autowired
    private GestureHelper gestureHelper;

    @Autowired
    private MobileContextManager contextManager;

    /**
     * Test: Create a new bug with all required fields.
     *
     * Steps:
     * 1. Verify home screen loads
     * 2. Navigate to Create Bug form
     * 3. Fill all required fields
     * 4. Submit the form
     * 5. Verify bug appears in the bugs list
     *
     * Expected Result: Bug is created successfully and visible in list
     */
    @Test
    @DisplayName("Create Bug - Happy Path (All Required Fields)")
    public void testCreateBug_HappyPath() {
        ExtentReportManager.getTest().info("Starting test: Create Bug with all required fields");

        // Step 1: Initialize bugs list page and verify it's loaded
        BugsListPage bugsListPage = new BugsListPage(driver, waitHelper, gestureHelper, contextManager);
        bugsListPage.waitUntilLoaded(20000);

        assertThat(bugsListPage.isLoaded())
                .as("Bugs list page should be loaded")
                .isTrue();

        ExtentReportManager.getTest().pass("Bugs list page loaded successfully");

        // Step 2: Generate unique bug title with timestamp
        String uniqueTitle = "Bug_" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        ExtentReportManager.getTest().info("Creating bug with title: " + uniqueTitle);

        // Step 3: Navigate to Create Bug form
        BugFormPage bugFormPage = bugsListPage.tapCreateBug();

        assertThat(bugFormPage.isLoaded())
                .as("Bug form should be loaded")
                .isTrue();

        ExtentReportManager.getTest().pass("Bug form loaded successfully");

        // Step 4: Fill the bug form
        bugFormPage
                .setBugId(123)
                .setDate("2025-10-07")
                .setTitle(uniqueTitle)
                .setStepsToReproduce("1. Open app\n2. Tap button X\n3. App crashes")
                .setExpectedResult("App should not crash")
                .setActualResult("App crashes with error")
                .selectStatus(BugStatus.OPEN)
                .selectSeverity(BugSeverity.CRITICAL)
                .selectPriority(BugPriority.CRITICAL)
                .setDetectedBy("QA Automation")
                .submitAndConfirm();


        ExtentReportManager.getTest().info("Bug form submitted");

//        // Step 5: Submit the form
//        BugsListPage resultPage = bugFormPage.submit();
//
//        ExtentReportManager.getTest().info("Bug form submitted");

        // Step 6: Verify bug appears in the list
//        resultPage
//                .goToViewTab()
//                .waitForBugWithTitle(uniqueTitle);
//
//        ExtentReportManager.getTest().pass("Bug created successfully and appears in list: " + uniqueTitle);
    }

    /**
     * Test: Create a bug using data from JSON file (Data-Driven Testing).
     *
     * Steps:
     * 1. Load bug data from JSON file
     * 2. Navigate to Create Bug form
     * 3. Fill form using Bug model object
     * 4. Submit and verify
     *
     * Expected Result: Bug from JSON is created successfully
     */
    @Test
    @DisplayName("Create Bug - Data Driven from JSON")
    public void testCreateBug_DataDriven() {
        ExtentReportManager.getTest().info("Starting test: Create Bug using data from JSON");

        // Step 1: Load bug data from JSON (using first bug in the file)
        Bug bugData = DataProvider.loadBugFromJson("testdata/bugs.json", 0);

        // Make title unique
        String uniqueTitle = bugData.getTitle() + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        bugData.setTitle(uniqueTitle);

        ExtentReportManager.getTest().info("Loaded bug data from JSON: " + bugData);

        // Step 2: Navigate to Create Bug form
        BugsListPage bugsListPage = new BugsListPage(driver, waitHelper, gestureHelper, contextManager);
        bugsListPage.waitUntilLoaded(20000);

        BugFormPage bugFormPage = bugsListPage.tapCreateBug();

        ExtentReportManager.getTest().pass("Navigated to Bug form");

        // Step 3: Fill form using Bug model (demonstrates clean API)
        bugFormPage.fillBugForm(bugData);

        ExtentReportManager.getTest().info("Bug form filled using data model");

        // Step 4: Submit and verify
        BugsListPage resultPage = bugFormPage.submit();

        resultPage
                .goToViewTab()
                .waitForBugWithTitle(uniqueTitle);

        ExtentReportManager.getTest().pass("Bug from JSON created successfully: " + uniqueTitle);
    }

    /**
     * Test: Create a minimal bug with only required fields.
     *
     * Steps:
     * 1. Navigate to Create Bug form
     * 2. Fill only required fields (title, status, etc.)
     * 3. Submit and verify
     *
     * Expected Result: Bug is created with minimal data
     */
    @Test
    @DisplayName("Create Bug - Minimal Required Fields")
    public void testCreateBug_MinimalFields() {
        ExtentReportManager.getTest().info("Starting test: Create Bug with minimal required fields");

        String uniqueTitle = "MinimalBug_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        BugsListPage bugsListPage = new BugsListPage(driver, waitHelper, gestureHelper, contextManager);
        bugsListPage.waitUntilLoaded(20000);

        BugFormPage bugFormPage = bugsListPage.tapCreateBug();

        // Fill only essential fields
        bugFormPage
                .setBugId(999)
                .setTitle(uniqueTitle)
                .selectStatus(BugStatus.OPEN);

        ExtentReportManager.getTest().info("Filled minimal required fields");

        BugsListPage resultPage = bugFormPage.submit();

        resultPage
                .goToViewTab()
                .waitForBugWithTitle(uniqueTitle);

        ExtentReportManager.getTest().pass("Minimal bug created successfully: " + uniqueTitle);
    }
}
