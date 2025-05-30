package com.playwrighttests.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public static boolean assertFieldEquals(String json, String fieldName, String valueName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode targetField = root.findValue(fieldName);
            if (targetField == null) {
                return false;
            }
            if (targetField.isArray()) {
                for (JsonNode element : targetField) {
                    if (element.asText().equals(valueName)) {
                        return true;
                    }
                }
            } else if (targetField.isTextual() || targetField.isNumber() || targetField.isBoolean()) {
                return targetField.asText().equals(valueName);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
