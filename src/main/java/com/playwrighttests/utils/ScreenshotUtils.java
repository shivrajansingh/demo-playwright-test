package com.playwrighttests.utils;

import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final ConfigManager config = ConfigManager.getInstance();

    /**
     * Take screenshot with custom name
     */
    public static String takeScreenshot(Page page, String testName) {
        if (!config.shouldCaptureScreenshots()) {
            log.debug("Screenshot capture is disabled");
            return null;
        }

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = String.format("%s_%s.png", testName, timestamp);
            Path screenshotPath = Paths.get(config.getScreenshotsPath(), fileName);

            // Create directory if it doesn't exist
            screenshotPath.getParent().toFile().mkdirs();

            page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath));
            log.info("Screenshot captured: {}", screenshotPath);
            return screenshotPath.toString();
        } catch (Exception e) {
            log.error("Failed to capture screenshot for test '{}': {}", testName, e.getMessage());
            return null;
        }
    }

    /**
     * Take screenshot on test failure
     */
    public static String takeScreenshotOnFailure(Page page, String testName) {
        String fileName = String.format("FAILED_%s", testName);
        return takeScreenshot(page, fileName);
    }

    /**
     * Take full page screenshot
     */
    public static String takeFullPageScreenshot(Page page, String testName) {
        if (!config.shouldCaptureScreenshots()) {
            log.debug("Screenshot capture is disabled");
            return null;
        }

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = String.format("%s_fullpage_%s.png", testName, timestamp);
            Path screenshotPath = Paths.get(config.getScreenshotsPath(), fileName);

            // Create directory if it doesn't exist
            screenshotPath.getParent().toFile().mkdirs();

            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(screenshotPath)
                    .setFullPage(true));
            log.info("Full page screenshot captured: {}", screenshotPath);
            return screenshotPath.toString();
        } catch (Exception e) {
            log.error("Failed to capture full page screenshot for test '{}': {}", testName, e.getMessage());
            return null;
        }
    }

    /**
     * Take screenshot of specific element
     */
    public static String takeElementScreenshot(Page page, String selector, String testName) {
        if (!config.shouldCaptureScreenshots()) {
            log.debug("Screenshot capture is disabled");
            return null;
        }

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = String.format("%s_element_%s.png", testName, timestamp);
            Path screenshotPath = Paths.get(config.getScreenshotsPath(), fileName);

            // Create directory if it doesn't exist
            screenshotPath.getParent().toFile().mkdirs();

            page.locator(selector).screenshot(new com.microsoft.playwright.Locator.ScreenshotOptions()
                    .setPath(screenshotPath));
            log.info("Element screenshot captured: {}", screenshotPath);
            return screenshotPath.toString();
        } catch (Exception e) {
            log.error("Failed to capture element screenshot for test '{}': {}", testName, e.getMessage());
            return null;
        }
    }
}
