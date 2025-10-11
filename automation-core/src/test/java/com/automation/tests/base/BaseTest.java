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
 * - Spring DI setup
 * - Reports integration
 * - Driver management
 * - Common test setup and teardown
 * - Shared dependencies (driver, helpers) to avoid duplications
 *
 * @author Rom
 * @version 1.0
 */
@ExtendWith({SpringExtension.class, TestListener.class})
@ContextConfiguration(classes = AutomationConfig.class)
public abstract class BaseTest {

    @Autowired
    protected DriverManager driverManager;
    @Autowired
    protected com.automation.utils.WaitHelper waitHelper;
    @Autowired
    protected com.automation.utils.GestureHelper gestureHelper;
    @Autowired
    protected io.appium.java_client.android.AndroidDriver driver;
    @Autowired
    protected com.automation.core.config.ConfigReader configReader;

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


    /**
     * Gets the NavigationBar component for navigation between screens.
     * @return NavigationBar instance for screen navigation
     */
    protected com.automation.pages.components.NavigationBar getNavigationBar() {
        return new com.automation.pages.components.NavigationBar(driver, waitHelper, gestureHelper, configReader);
    }
}
