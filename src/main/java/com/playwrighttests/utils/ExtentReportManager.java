package com.playwrighttests.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ExtentReportManager {
    private static final Logger log = LoggerFactory.getLogger(ExtentReportManager.class);
    private static final ExtentReports extentReports = new ExtentReports();
    private static final Map<String, ExtentTest> testMap = new HashMap<>();
    private static ExtentReportManager instance;
    private static String reportPath;

    private ExtentReportManager() {
        initialize();
    }

    public static synchronized ExtentReportManager getInstance() {
        if (instance == null) {
            instance = new ExtentReportManager();
        }
        return instance;
    }

    private void initialize() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            reportPath = "test-results/reports/ExtentReport_" + timestamp + ".html";

            // Create reports directory if it doesn't exist
            new File(reportPath).getParentFile().mkdirs();

            // Initialize ExtentSparkReporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("Playwright Test Report");
            sparkReporter.config().setReportName("Test Automation Results");
            sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");

            // Attach reporter to ExtentReports
            extentReports.attachReporter(sparkReporter);

            // Add system info
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("Browser", ConfigManager.getInstance().getBrowserType());

            log.info("ExtentReports initialized successfully. Report will be generated at: {}", reportPath);
        } catch (Exception e) {
            log.error("Failed to initialize ExtentReports: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize ExtentReports", e);
        }
    }

    public synchronized ExtentTest createTest(String testName, String description) {
        ExtentTest test = extentReports.createTest(testName, description);
        testMap.put(testName, test);
        return test;
    }

    public synchronized ExtentTest getTest(String testName) {
        return testMap.get(testName);
    }

    public void addScreenshot(String testName, String screenshotPath) {
        try {
            ExtentTest test = getTest(testName);
            if (test != null && screenshotPath != null) {
                test.addScreenCaptureFromPath(screenshotPath);
            }
        } catch (Exception e) {
            log.error("Failed to add screenshot to report: {}", e.getMessage());
        }
    }

    public void addInfo(String testName, String message) {
        ExtentTest test = getTest(testName);
        if (test != null) {
            test.info(message);
        }
    }

    public void addError(String testName, String message) {
        ExtentTest test = getTest(testName);
        if (test != null) {
            test.fail(new RuntimeException(message));
        }
    }

    public void addFailure(String testName, String message) {
        ExtentTest test = getTest(testName);
        if (test != null) {
            test.fail(new AssertionError(message));
        }
    }

    public void addSuccess(String testName, String message) {
        ExtentTest test = getTest(testName);
        if (test != null) {
            test.pass(message);
        }
    }

    public void addSkipped(String testName, String message) {
        ExtentTest test = getTest(testName);
        if (test != null) {
            test.skip(message);
        }
    }

    public synchronized void flush() {
        extentReports.flush();
    }

    public String getReportPath() {
        return reportPath;
    }
}
