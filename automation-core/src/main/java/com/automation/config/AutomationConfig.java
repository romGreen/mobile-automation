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
 * Spring Configuration class for the framework.
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
     * This bean is used in the framework to read configuration values.
     * @return ConfigReader instance
     */
    @Bean
    public ConfigReader configReader() {
        return new JsonConfigReader();
    }

    /**
     * Creates a DriverManager bean.
     * The driver lifecycle (start/stop) is managed by BaseTest.
     * @param configReader The config reader (injected by Spring)
     * @return DriverManager instance
     */
    @Bean
    public DriverManager driverManager(ConfigReader configReader) {
        return new AppiumDriverManager(configReader);
    }

    /**
     * Creates an AndroidDriver bean.
     * Prototype scope ensures each injection gets a fresh instance.
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
