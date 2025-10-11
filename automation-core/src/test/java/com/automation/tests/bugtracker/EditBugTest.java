package com.automation.tests.bugtracker;

import com.automation.enums.BugPriority;
import com.automation.enums.BugSeverity;
import com.automation.enums.BugStatus;
import com.automation.pages.bugtracker.CreateBugPage;
import com.automation.pages.bugtracker.BugsListPage;
import com.automation.pages.bugtracker.EditBugPage;
import com.automation.pages.locators.BugTrackerLocators;
import com.automation.reporting.ExtentReportManager;
import com.automation.tests.base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for editing bugs.
 * Test Coverage:
 * - Edit bug status from Open to Closed
 *
 * @author Rom
 * @version 1.0
 */
public class EditBugTest extends BaseTest {
    /**
     * Test: Create a bug and edit its status from Open to Fixed.
     *
     * Steps:
     * 1. Create a new bug with status Open
     * 2. Navigate to View Bugs
     * 3. Click on the created bug
     * 4. Change status to Fixed + bonus change date closed field
     * 5. Save the change
     * 6. Verify the bug in Closed list
     *
     * Expected Result: Bug status is successfully changed
     */
    @Test
    @DisplayName("Edit Bug Status - Open to Closed")
    public void testEditBugStatus_OpenToClosed() {
        ExtentReportManager.getTest().info("Starting test: Edit bug status from Open to Closed");

        // Step 1: Create a bug
        int bugId = 905;  // Store bug ID in variable for reuse
        String Title = "Edit Test";

        // Navigate to Create Bug form from anywhere
        CreateBugPage bugFormPage = getNavigationBar().goToCreateBug();

        bugFormPage
                .setBugId(bugId)
                .setDate("08.09.2025")
                .setTitle(Title)
                .setStepsToRecreate("Steps to recreate")
                .setExpectedResult("Expected result")
                .setActualResult("Actual result")
                .selectStatus(BugStatus.OPEN)
                .selectSeverity(BugSeverity.MAJOR)
                .selectPriority(BugPriority.HIGH)
                .setDetectedBy("QA")
                .setFixedBy("Alice")
                .setDateClosed("19.09.2025")
                .attachFile();


        BugsListPage resultPage = bugFormPage.submit();
        ExtentReportManager.getTest().info("Created bug with ID: " + bugId + ", title: " + Title);

        // Step 2: Navigate to View Bugs
        resultPage.navigationBar.goToViewBugs();
        resultPage.waitForBugWithId(bugId);

        ExtentReportManager.getTest().pass("Bug created successfully");

        // Step 3: Click Edit button for the bug using the Bug ID
        EditBugPage editForm = resultPage.clickEditButtonForBugById(bugId);

        ExtentReportManager.getTest().info("Opened bug for editing");

        // Step 4: Change status to Closed + bonus change date closed
        editForm.selectStatus(BugStatus.CLOSED);
        editForm.setDateClosed("22.09.2025");
        editForm.attachFile();

        ExtentReportManager.getTest().info("Changed status to Closed");

        // Step 5: Submit the changes(Save changes)
        BugsListPage finalPage = editForm.submit();

        // Step 6: Click "closed" filter to see if bug status is now "closed"
        finalPage.clickStatusFilter(BugTrackerLocators.FILTER_CLOSED_TEXT);

        ExtentReportManager.getTest().info("Clicked Closed filter");

        // Step 7: Verify bug appears in Closed filter
        boolean isClosed = finalPage.isBugInCurrentFilter(bugId);

        assertThat(isClosed)
                .as("Bug should appear in Closed filter after status change")
                .isTrue();

        ExtentReportManager.getTest().pass("Bug status verified successfully - found in Closed filter");
    }
}
