package com.playwrighttests.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for making assertions on API responses
 */
public class ResponseAssert {
    private static final Logger log = LoggerFactory.getLogger(ResponseAssert.class);
    
    private ResponseAssert() {
        // Prevent instantiation
    }
    
    /**
     * Assert that the response has a successful status code (2xx)
     * 
     * @param response the API response
     */
    public static void assertSuccess(APIResponse response) {
        int status = response.status();
        Assertions.assertTrue(status >= 200 && status < 300, 
                String.format("Expected successful status code, but got %d", status));
    }
    
    /**
     * Assert that the response has the expected status code
     * 
     * @param response the API response
     * @param expectedStatus the expected status code
     */
    public static void assertStatus(APIResponse response, int expectedStatus) {
        Assertions.assertEquals(expectedStatus, response.status(), 
                String.format("Expected status code %d, but got %d", expectedStatus, response.status()));
    }
    
    /**
     * Assert that the response contains the specified text
     * 
     * @param response the API response
     * @param expectedText the expected text
     */
    public static void assertContainsText(APIResponse response, String expectedText) {
        String responseText = response.text();
        Assertions.assertTrue(responseText.contains(expectedText), 
                String.format("Expected response to contain '%s'", expectedText));
    }
    
    /**
     * Assert that the response body contains a field
     * 
     * @param response the API response
     * @param fieldName the field name to check for
     */
    public static void assertContainsField(APIResponse response, String fieldName) {
        String responseText = response.text();
        Assertions.assertTrue(responseText.contains(String.format("\"%s\"", fieldName)), 
                String.format("Expected response to contain field '%s'", fieldName));
    }
    
    /**
     * Assert that the response body has a field with a specific value
     * 
     * @param response the API response
     * @param fieldName the field name to check
     * @param expectedValue the expected field value
     */
    public static void assertFieldEquals(APIResponse response, String fieldName, String expectedValue) {
        String responseText = response.text();
        
        // Print a well-formatted version of the JSON response for debugging
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = JsonParser.parseString(responseText);
        String prettyJson = gson.toJson(je);
        
        log.info("Checking if response contains field '{}' with value '{}'", fieldName, expectedValue);
        log.info("Response JSON structure:\n{}", prettyJson);
        
        try {
            // Create a simple JSON object from response text
            JsonObject jsonObject = JsonParser.parseString(responseText).getAsJsonObject();
            
            // Find the field at the root or in the payload
            boolean fieldFound = false;
            String actualValue = null;
            
            // Check at root level
            if (jsonObject.has(fieldName)) {
                JsonElement element = jsonObject.get(fieldName);
                if (element.isJsonPrimitive()) {
                    actualValue = element.getAsString();
                    log.info("Found field '{}' at root level with value: '{}'", fieldName, actualValue);
                    fieldFound = expectedValue.equals(actualValue);
                }
            }
            
            // Check in payload if exists and field not found at root
            if (!fieldFound && jsonObject.has("payload")) {
                JsonObject payload = jsonObject.getAsJsonObject("payload");
                if (payload.has(fieldName)) {
                    JsonElement element = payload.get(fieldName);
                    if (element.isJsonPrimitive()) {
                        actualValue = element.getAsString();
                        log.info("Found field '{}' in payload with value: '{}'", fieldName, actualValue);
                        fieldFound = expectedValue.equals(actualValue);
                    }
                }
            }
            
            // If field was found but values don't match, report the actual value
            if (actualValue != null && !fieldFound) {
                Assertions.fail(String.format("Field '%s' found but value was '%s', expected '%s'", 
                    fieldName, actualValue, expectedValue));
            } else if (!fieldFound) {
                // Field was not found at all
                Assertions.fail(String.format("Field '%s' not found in response", fieldName));
            } else {
                // Assert that we found the field with the expected value
                Assertions.assertTrue(fieldFound, 
                    String.format("Expected response to contain field '%s' with value '%s'", fieldName, expectedValue));
            }
        } catch (Exception e) {
            log.error("Error parsing JSON response: {}", e.getMessage(), e);
            Assertions.fail(String.format("Error checking field '%s' with value '%s': %s", 
                fieldName, expectedValue, e.getMessage()));
        }
    }
}
