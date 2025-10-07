package com.automation.config;

import com.automation.core.config.ConfigReader;
import com.automation.core.config.JsonConfigReader;
import com.automation.core.driver.AppiumDriverManager;
import com.automation.core.driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Spring Configuration class for the automation framework.
 *
 * This class defines Spring beans and their dependencies, enabling
 * dependency injection throughout the framework.
 *
 * Demonstrates OOP principles:
 * - Dependency Injection: Objects don't create their dependencies
 * - Inversion of Control: Spring container manages object lifecycle
 * - Single Responsibility: Configuration in one place
 * - Loose Coupling: Components depend on interfaces, not implementations
 *
 * @author Automation Team
 * @version 1.0
 */
@Configuration
@ComponentScan(basePackages = {
        "com.automation.core",
        "com.automation.pages",
        "com.automation.utils",
        "com.automation.reporting"
})
public class AutomationConfig {

    /**
     * Creates a ConfigReader bean.
     *
     * This bean is used throughout the framework to read configuration values.
     * Singleton scope ensures only one instance exists.
     *
     * @return ConfigReader instance
     */
    @Bean
    public ConfigReader configReader() {
        return new JsonConfigReader();
    }

    /**
     * Creates a DriverManager bean.
     *
     * The DriverManager handles driver lifecycle and is injected into
     * components that need driver access.
     *
     * @param configReader The config reader (injected by Spring)
     * @return DriverManager instance
     */
    @Bean
    public DriverManager driverManager(ConfigReader configReader) {
        return new AppiumDriverManager(configReader);
    }

    /**
     * Creates an AndroidDriver bean with prototype scope.
     *
     * Prototype scope means a new instance is created each time it's requested.
     * The driver is started automatically when this bean is created.
     *
     * @param driverManager The driver manager (injected by Spring)
     * @return AndroidDriver instance
     */
    @Bean
    @Scope("prototype")
    public AndroidDriver androidDriver(DriverManager driverManager) {
        if (!driverManager.isDriverActive()) {
            driverManager.startDriver();
        }
        return driverManager.getDriver();
    }
}
