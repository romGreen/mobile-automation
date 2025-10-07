package com.automation.tests.base;

import com.automation.config.AutomationConfig;
import com.automation.core.driver.DriverManager;
import com.automation.reporting.ExtentReportManager;
import com.automation.reporting.TestListener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Abstract base class for all test classes.
 *
 * This class provides:
 * - Spring dependency injection setup
 * - Extent Reports integration
 * - Driver lifecycle management
 * - Common test setup/teardown
 *
 * Demonstrates OOP principles:
 * - Inheritance: All tests extend this class
 * - Code reuse: Common test setup in one place
 * - Dependency Injection: Spring manages dependencies
 *
 * @author Automation Team
 * @version 1.0
 */
@ExtendWith({SpringExtension.class, TestListener.class})
@ContextConfiguration(classes = AutomationConfig.class)
public abstract class BaseTest {

    @Autowired
    protected DriverManager driverManager;

    /**
     * One-time setup before all tests in the class.
     * Initializes Extent Reports.
     */
    @BeforeAll
    public static void globalSetup() {
        ExtentReportManager.getInstance();
    }

    /**
     * Setup before each test method.
     * Starts the driver session.
     */
    @BeforeEach
    public void setUp() {
        driverManager.startDriver();
    }

    /**
     * Teardown after each test method.
     * Stops the driver session.
     */
    @AfterEach
    public void tearDown() {
        driverManager.stopDriver();
    }

    /**
     * One-time teardown after all tests in the class.
     * Flushes Extent Reports.
     */
    @AfterAll
    public static void globalTeardown() {
        ExtentReportManager.flush();
    }
}
