package com.automation.tests.bugtracker;

import com.automation.core.context.MobileContextManager;
import com.automation.enums.BugPriority;
import com.automation.enums.BugSeverity;
import com.automation.enums.BugStatus;
import com.automation.pages.bugtracker.BugFormPage;
import com.automation.pages.bugtracker.BugsListPage;
import com.automation.pages.bugtracker.EditBugPage;
import com.automation.reporting.ExtentReportManager;
import com.automation.tests.base.BaseTest;
import com.automation.utils.GestureHelper;
import com.automation.utils.WaitHelper;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for editing existing bugs.
 *
 * This class tests the ability to edit bug properties,
 * specifically changing the status of an existing bug.
 *
 * Test Coverage:
 * - Edit bug status from Open to Fixed
 * - Edit bug status from Open to Closed
 *
 * @author Automation Team
 * @version 1.0
 */
public class EditBugTest extends BaseTest {

    @Autowired
    private AndroidDriver driver;

    @Autowired
    private WaitHelper waitHelper;

    @Autowired
    private GestureHelper gestureHelper;

    @Autowired
    private MobileContextManager contextManager;

    /**
     * Test: Create a bug and edit its status from Open to Fixed.
     *
     * Steps:
     * 1. Create a new bug with status Open
     * 2. Navigate to View Bugs
     * 3. Click on the created bug
     * 4. Change status to Fixed
     * 5. Save the bug
     * 6. Verify the bug still exists in the list
     *
     * Expected Result: Bug status is successfully changed
     */
    @Test
    @DisplayName("Edit Bug Status - Open to Fixed")
    public void testEditBugStatus_OpenToFixed() {
        ExtentReportManager.getTest().info("Starting test: Edit bug status from Open to Fixed");

        // Step 1: Create a bug
        String uniqueTitle = "EditTest_" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        BugsListPage bugsListPage = new BugsListPage(driver, waitHelper, gestureHelper, contextManager);
        bugsListPage.waitUntilLoaded(20000);

        BugFormPage bugFormPage = bugsListPage.tapCreateBug();

        bugFormPage
                .setBugId(888)
                .setDate("2025-10-08")
                .setTitle(uniqueTitle)
                .setStepsToReproduce("Steps to reproduce")
                .setExpectedResult("Expected result")
                .setActualResult("Actual result")
                .setDetectedBy("QA Team");

        ExtentReportManager.getTest().info("Created bug with title: " + uniqueTitle);

        BugsListPage resultPage = bugFormPage.submit();
        resultPage.goToViewTab();
        resultPage.waitForBugWithTitle(uniqueTitle);

        ExtentReportManager.getTest().pass("Bug created successfully");

        // Step 2: Click Edit button for the bug
        EditBugPage editForm = resultPage.clickEditButtonForBug(uniqueTitle);

        assertThat(editForm.isLoaded())
                .as("Edit form should be loaded")
                .isTrue();

        ExtentReportManager.getTest().info("Opened bug for editing");

        // Step 3: Change status to Fixed
        editForm.selectStatus(BugStatus.FIXED);

        ExtentReportManager.getTest().info("Changed status to Fixed");

        // Step 4: Submit the changes
        BugsListPage finalPage = editForm.submit();

        // Step 5: Verify bug still exists in View Bugs
        finalPage.waitForBugWithTitle(uniqueTitle);

        ExtentReportManager.getTest().info("Bug found in list after editing");

        // Step 6: Click "Fixed" filter to verify status change
        finalPage.clickStatusFilter("Fixed");

        ExtentReportManager.getTest().info("Clicked Fixed filter");

        // Step 7: Verify bug appears in Fixed filter
        boolean isFixed = finalPage.isBugInCurrentFilter(uniqueTitle);

        assertThat(isFixed)
                .as("Bug should appear in Fixed filter after status change")
                .isTrue();

        ExtentReportManager.getTest().pass("Bug status verified successfully - found in Fixed filter");
    }
}
