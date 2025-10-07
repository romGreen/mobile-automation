package com.automation.reporting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JUnit 5 Extension for integrating tests with Extent Reports.
 *
 * This listener automatically:
 * - Creates test entries in the report
 * - Logs test pass/fail status
 * - Captures test execution time
 * - Logs failure details and stack traces
 *
 * Demonstrates OOP principles:
 * - Observer pattern: Listens to test lifecycle events
 * - Single Responsibility: Handles only test reporting
 *
 * Usage:
 * Add @ExtendWith(TestListener.class) to test classes
 *
 * @author Automation Team
 * @version 1.0
 */
public class TestListener implements BeforeAllCallback, BeforeEachCallback,
        AfterEachCallback, AfterAllCallback, TestWatcher {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        log.info("Starting test suite: {}", context.getDisplayName());
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        String testName = context.getDisplayName();
        String testClass = context.getTestClass()
                .map(Class::getSimpleName)
                .orElse("Unknown");

        String description = String.format("Test class: %s", testClass);

        ExtentTest test = ExtentReportManager.createTest(testName, description);
        test.info("Test execution started");

        log.info("Starting test: {}", testName);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        log.info("Finished test: {}", context.getDisplayName());
    }

    @Override
    public void afterAll(ExtensionContext context) {
        log.info("Finished test suite: {}", context.getDisplayName());
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.PASS,
                    MarkupHelper.createLabel("Test PASSED", ExtentColor.GREEN));
            test.pass("Test completed successfully");
        }
        ExtentReportManager.removeTest();
        log.info("Test PASSED: {}", context.getDisplayName());
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.FAIL,
                    MarkupHelper.createLabel("Test FAILED", ExtentColor.RED));
            test.fail("Test failed with error: " + cause.getMessage());
            test.fail(cause);

            // Log stack trace
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement element : cause.getStackTrace()) {
                stackTrace.append(element.toString()).append("\n");
            }
            test.info("<pre>" + stackTrace.toString() + "</pre>");
        }
        ExtentReportManager.removeTest();
        log.error("Test FAILED: {}", context.getDisplayName(), cause);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.SKIP,
                    MarkupHelper.createLabel("Test SKIPPED/ABORTED", ExtentColor.ORANGE));
            test.skip("Test was aborted: " + cause.getMessage());
        }
        ExtentReportManager.removeTest();
        log.warn("Test ABORTED: {}", context.getDisplayName(), cause);
    }

    @Override
    public void testDisabled(ExtensionContext context, java.util.Optional<String> reason) {
        ExtentTest test = ExtentReportManager.createTest(
                context.getDisplayName(),
                "Test was disabled");

        test.log(Status.SKIP,
                MarkupHelper.createLabel("Test DISABLED", ExtentColor.GREY));

        reason.ifPresent(r -> test.skip("Reason: " + r));

        ExtentReportManager.removeTest();
        log.info("Test DISABLED: {} - Reason: {}",
                context.getDisplayName(),
                reason.orElse("No reason provided"));
    }
}
