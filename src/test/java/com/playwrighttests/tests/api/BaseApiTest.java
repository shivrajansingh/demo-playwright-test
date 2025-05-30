package com.playwrighttests.tests.api;

import com.microsoft.playwright.Playwright;
import com.playwrighttests.utils.ConfigManager;
import com.playwrighttests.utils.ExtentReportManager;
import com.playwrighttests.utils.Service;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for API tests
 */
public abstract class BaseApiTest {
    private static final Logger log = LoggerFactory.getLogger(BaseApiTest.class);
    protected static Playwright playwright;
    protected Service service;
    protected static ConfigManager config;
    protected static ExtentReportManager reportManager;
    
    @BeforeAll
    static void globalSetup() {
        log.info("=== API Test Global Setup Started ===");
        config = ConfigManager.getInstance();
        reportManager = ExtentReportManager.getInstance();
        log.info("=== API Test Global Setup Completed ===");
    }
    
    @BeforeEach
    void setUp(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        log.info("=== API Test Setup Started: {} ===", testName);
        
        // Create test in report
        String testDescription = testInfo.getTags().isEmpty() ? 
            "API Test execution for " + testName : 
            String.join(", ", testInfo.getTags());
        
        reportManager.createTest(testName, testDescription);
        reportManager.addInfo(testName, "API Test execution started");
        
        // Create service
        service = new Service();
        
        log.info("=== API Test Setup Completed: {} ===", testName);
    }
    
    @AfterEach
    void tearDown(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        log.info("=== API Test Teardown Started: {} ===", testName);
        
        try {
            // Close service
            if (service != null) {
                service.close();
            }
            
            // Update report
            reportManager.addInfo(testName, "API Test teardown completed");
        } catch (Exception e) {
            String errorMsg = "Error in API test teardown: " + e.getMessage();
            log.error(errorMsg, e);
            reportManager.addError(testName, errorMsg);
        }
        
        log.info("=== API Test Teardown Completed: {} ===", testName);
    }
    
    @AfterAll
    static void globalTeardown() {
        log.info("=== API Test Global Teardown Started ===");
        if (reportManager != null) {
            reportManager.flush();
            log.info("Report generated at: {}", reportManager.getReportPath());
        }
        log.info("=== API Test Global Teardown Completed ===");
    }
}
