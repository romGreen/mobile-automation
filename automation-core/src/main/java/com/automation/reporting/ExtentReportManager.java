package com.automation.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manager for Extent Reports HTML test reporting.
 *
 * This class provides centralized management of Extent Reports,
 * handling report initialization, test creation, and finalization.
 * @author Rom
 * @version 1.0
 */
public class ExtentReportManager {

    private static final Logger log = LogManager.getLogger(ExtentReportManager.class);

    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    private static final String REPORT_DIRECTORY = "test-output/extent-reports/";
    private static final String REPORT_NAME = "AutomationTestReport";

    /**
     * Private constructor to prevent instantiation (Singleton pattern).
     */
    private ExtentReportManager() {
        throw new UnsupportedOperationException("Utility class - do not instantiate");
    }

    /**
     * Initializes the Extent Reports instance.
     * This should be called once before all tests run.
     *
     * @return The initialized ExtentReports instance
     */
    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            createInstance();
        }
        return extentReports;
    }

    /**
     * Creates a new Extent Reports instance with configuration.
     */
    private static void createInstance() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportPath = REPORT_DIRECTORY + REPORT_NAME + "_" + timestamp + ".html";

        // Create directory if it doesn't exist
        File reportDir = new File(REPORT_DIRECTORY);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        // Create Spark reporter (HTML)
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

        // Configure reporter
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("Mobile Automation Test Report");
        sparkReporter.config().setReportName("Bug Tracker Automation Results");
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

        // Create Extent Reports instance
        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);

        // Set system information
        extentReports.setSystemInfo("Application", "ATID Bug Tracker");
        extentReports.setSystemInfo("Platform", "Android");
        extentReports.setSystemInfo("Automation Tool", "Appium + Java");
        extentReports.setSystemInfo("Framework", "JUnit 5 + Spring");
        extentReports.setSystemInfo("Report Generated", timestamp);

        log.info("Extent Report initialized: {}", reportPath);
    }

    /**
     * Creates a new test entry in the report.
     *
     * @param testName        The name of the test
     * @param testDescription Description of what the test does
     * @return The created ExtentTest instance
     */
    public static ExtentTest createTest(String testName, String testDescription) {
        ExtentTest test = getInstance().createTest(testName, testDescription);
        extentTest.set(test);
        log.debug("Created test in report: {}", testName);
        return test;
    }

    /**
     * Gets the current test instance for the executing thread.
     *
     * @return The current ExtentTest instance
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }

    /**
     * Finalizes and writes the report to disk.
     * This should be called after all tests have completed.
     */
    public static synchronized void flush() {
        if (extentReports != null) {
            extentReports.flush();
            log.info("Extent Report flushed successfully");
        }
    }

    /**
     * Removes the current test from ThreadLocal storage.
     * Call this after each test to prevent memory leaks.
     */
    public static void removeTest() {
        extentTest.remove();
    }
}
