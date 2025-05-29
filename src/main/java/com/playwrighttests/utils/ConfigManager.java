package com.playwrighttests.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager {
    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties;
    private static ConfigManager instance;

    private ConfigManager() {
        loadProperties();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
                log.info("Configuration properties loaded successfully");
            } else {
                log.error("Configuration file '{}' not found in classpath", CONFIG_FILE);
                throw new RuntimeException("Configuration file not found: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            log.error("Error loading configuration properties: {}", e.getMessage());
            throw new RuntimeException("Failed to load configuration properties", e);
        }
    }

    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            log.warn("Property '{}' not found in configuration", key);
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    public int getIntProperty(String key) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (NumberFormatException e) {
            log.error("Invalid integer value for property '{}': {}", key, getProperty(key));
            throw new RuntimeException("Invalid integer property: " + key);
        }
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            String value = getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            log.warn("Invalid integer value for property '{}', using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    // Specific configuration getters
    public String getBrowserType() {
        return getProperty("browser.type", "chromium");
    }

    public boolean isHeadless() {
        return getBooleanProperty("browser.headless");
    }

    public int getBrowserTimeout() {
        return getIntProperty("browser.timeout", 30000);
    }

    public String getBaseUrl(String key) {
        return getProperty("base.url."+key);
    }

    public String getApiBaseUrl() {
        return getProperty("api.base.url");
    }

    public boolean shouldCaptureScreenshots() {
        return getBooleanProperty("capture.screenshots");
    }

    public boolean shouldCaptureVideos() {
        return getBooleanProperty("capture.videos");
    }

    public String getScreenshotsPath() {
        return getProperty("screenshots.path", "test-results/screenshots");
    }

    public String getVideosPath() {
        return getProperty("videos.path", "test-results/videos");
    }

    public int getMaxRetryCount() {
        return getIntProperty("max.retry.count", 2);
    }

    public int getMaxParallelThreads() {
        return getIntProperty("max.parallel.threads", 4);
    }
}
