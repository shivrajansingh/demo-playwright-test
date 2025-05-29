package com.playwrighttests.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.playwrighttests.utils.ConfigManager;
import com.playwrighttests.utils.ScreenshotUtils;
import com.playwrighttests.utils.ExtentReportManager;

import java.nio.file.Paths;

public abstract class BaseTest {
    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected static ConfigManager config;
    protected static ExtentReportManager reportManager;

    @BeforeAll
    static void globalSetup() {
        log.info("=== Global Test Setup Started ===");
        config = ConfigManager.getInstance();
        reportManager = ExtentReportManager.getInstance();
        
        // Initialize Playwright
        playwright = Playwright.create();
        
        // Setup browser based on configuration
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(config.isHeadless())
                .setSlowMo(config.getIntProperty("browser.slowmo", 0));

        String browserType = config.getBrowserType().toLowerCase();
        browser = switch (browserType) {
            case "firefox" -> playwright.firefox().launch(launchOptions);
            case "safari", "webkit" -> playwright.webkit().launch(launchOptions);
            case "edge" -> playwright.chromium().launch(launchOptions.setChannel("msedge"));
            default -> playwright.chromium().launch(launchOptions);
        };

        log.info("Browser {} launched successfully", browserType);
        log.info("=== Global Test Setup Completed ===");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        String testDescription = testInfo.getTags().isEmpty() ? 
            "Test execution for " + testName : 
            String.join(", ", testInfo.getTags());
        
        reportManager.createTest(testName, testDescription);
        reportManager.addInfo(testName, "Test execution started");
        
        log.info("=== Test Setup Started: {} ===", testName);

        // Create new browser context for each test
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(
                    config.getIntProperty("browser.viewport.width", 1920),
                    config.getIntProperty("browser.viewport.height", 1080)
                );

        // Configure video recording if enabled
        if (config.shouldCaptureVideos()) {
            contextOptions.setRecordVideoDir(Paths.get(config.getVideosPath()));
        }

        context = browser.newContext(contextOptions);
        
        // Set default timeout
        context.setDefaultTimeout(config.getBrowserTimeout());
        
        // Create new page
        page = context.newPage();

        // Add console listener for debugging
        page.onConsoleMessage(msg -> {
            String consoleMsg = String.format("Browser Console [%s]: %s", msg.type(), msg.text());
            log.debug(consoleMsg);
        });

        log.info("=== Test Setup Completed: {} ===", testName);
        reportManager.addInfo(testName, "Test setup completed successfully");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        log.info("=== Test Teardown Started: {} ===", testName);

        try {
            // Take screenshot on test failure
            if (testInfo.getTags().contains("failed")) {
                String screenshotPath = ScreenshotUtils.takeScreenshotOnFailure(page, testName);
                if (screenshotPath != null) {
                    reportManager.addScreenshot(testName, screenshotPath);
                    reportManager.addFailure(testName, "Test failed. Screenshot captured: " + screenshotPath);
                }
            } else {
                reportManager.addSuccess(testName, "Test completed successfully");
            }

            // Close page and context
            if (page != null) {
                page.close();
            }
            if (context != null) {
                context.close();
            }
        } catch (Exception e) {
            String errorMsg = "Error in test teardown: " + e.getMessage();
            log.error(errorMsg, e);
            reportManager.addError(testName, errorMsg);
        }

        log.info("=== Test Teardown Completed: {} ===", testName);
        reportManager.addInfo(testName, "Test teardown completed");
    }

    @AfterAll
    static void globalTearDown() {
        log.info("=== Global Test Teardown Started ===");
        
        try {
            if (browser != null) {
                browser.close();
            }
            if (playwright != null) {
                playwright.close();
            }

            // Generate the report
            if (reportManager != null) {
                reportManager.flush();
                log.info("Test report generated successfully at: {}", reportManager.getReportPath());
            }
        } catch (Exception e) {
            log.error("Error in global teardown", e);
        }

        log.info("=== Global Test Teardown Completed ===");
    }

    // Helper methods for common operations
    protected void navigateToBaseUrl() {
        String baseUrl = config.getBaseUrl("youtube");
        log.info("Navigating to base URL: {}", baseUrl);
        page.navigate(baseUrl);
    }

    protected void navigateTo(String url) {
        log.info("Navigating to URL: {}", url);
        page.navigate(url);
    }

    protected void waitForPageLoad() {
        page.waitForLoadState();
    }

    protected void waitForNetworkIdle() {
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    protected String getCurrentUrl() {
        return page.url();
    }

    protected String getPageTitle() {
        return page.title();
    }

    protected void takeScreenshot(String name) {
        String screenshotPath = ScreenshotUtils.takeScreenshot(page, name);
        if (screenshotPath != null) {
            reportManager.addScreenshot(name, screenshotPath);
        }
    }
}

