package com.playwrighttests.utils;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Core API client using Playwright's APIRequestContext
 */
public class ApiClient implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(ApiClient.class);
    
    private final Playwright playwright;
    private final APIRequestContext requestContext;
    private final ConfigManager config;
    
    /**
     * Creates a new ApiClient with default configuration
     */
    public ApiClient() {
        this(null);
    }
    
    /**
     * Creates a new ApiClient with custom headers
     * 
     * @param headers Custom headers to include in all requests
     */
    public ApiClient(Map<String, String> headers) {
        config = ConfigManager.getInstance();
        playwright = Playwright.create();
        
        Map<String, String> allHeaders = new HashMap<>();
        // Add default content type if not overridden
        allHeaders.put("Content-Type", config.getProperty("api.default.content.type", "application/json"));
        allHeaders.put("Accept", "application/json");
        
        // Add custom headers if provided
        if (headers != null && !headers.isEmpty()) {
            allHeaders.putAll(headers);
        }
        
        // Create the API request context
        requestContext = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(config.getProperty("api.base.url"))
                .setExtraHTTPHeaders(allHeaders)
                .setTimeout(config.getIntProperty("api.timeout", 30000)));
        
        log.info("API client initialized with base URL: {}", config.getProperty("api.base.url"));
    }
    
    /**
     * Get the underlying APIRequestContext
     * 
     * @return the APIRequestContext
     */
    public APIRequestContext getRequestContext() {
        return requestContext;
    }
    
    /**
     * Send an HTTP GET request
     * 
     * @param url The endpoint URL (relative to base URL)
     * @return APIResponse object containing the response
     */
    public APIResponse get(String url) {
        log.info("Sending GET request to: {}", url);
        APIResponse response = requestContext.get(url);
        logResponse(response);
        return response;
    }
    
    /**
     * Send an HTTP GET request with query parameters
     * 
     * @param url The endpoint URL (relative to base URL)
     * @param queryParams Map of query parameters
     * @return APIResponse object containing the response
     */
    public APIResponse get(String url, Map<String, String> queryParams) {
        log.info("Sending GET request to: {} with params: {}", url, queryParams);
        
        // Converting the Map to proper format for Playwright
        RequestOptions options = RequestOptions.create();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            options.setQueryParam(entry.getKey(), entry.getValue());
        }
        
        APIResponse response = requestContext.get(url, options);
        logResponse(response);
        return response;
    }
    
    /**
     * Send an HTTP POST request with a JSON body
     * 
     * @param url The endpoint URL (relative to base URL)
     * @param jsonBody JSON body as a string
     * @return APIResponse object containing the response
     */
    public APIResponse post(String url, String jsonBody) {
        log.info("Sending POST request to: {}", url);
        log.debug("POST request body: {}", jsonBody);
        RequestOptions options = RequestOptions.create().setData(jsonBody);
        APIResponse response = requestContext.post(url, options);
        logResponse(response);
        return response;
    }
    
    /**
     * Send an HTTP POST request with form data
     * 
     * @param url The endpoint URL (relative to base URL)
     * @param formData Map of form data
     * @return APIResponse object containing the response
     */
    public APIResponse postForm(String url, Map<String, String> formData) {
        log.info("Sending POST form request to: {}", url);
        log.debug("POST form data: {}", formData);
        
        // Create FormData object properly
        com.microsoft.playwright.options.FormData form = com.microsoft.playwright.options.FormData.create();
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            form.set(entry.getKey(), entry.getValue());
        }
        
        RequestOptions options = RequestOptions.create().setForm(form);
        APIResponse response = requestContext.post(url, options);
        logResponse(response);
        return response;
    }
    
    /**
     * Send an HTTP PUT request with a JSON body
     * 
     * @param url The endpoint URL (relative to base URL)
     * @param jsonBody JSON body as a string
     * @return APIResponse object containing the response
     */
    public APIResponse put(String url, String jsonBody) {
        log.info("Sending PUT request to: {}", url);
        log.debug("PUT request body: {}", jsonBody);
        RequestOptions options = RequestOptions.create().setData(jsonBody);
        APIResponse response = requestContext.put(url, options);
        logResponse(response);
        return response;
    }
    
    /**
     * Send an HTTP PATCH request with a JSON body
     * 
     * @param url The endpoint URL (relative to base URL)
     * @param jsonBody JSON body as a string
     * @return APIResponse object containing the response
     */
    public APIResponse patch(String url, String jsonBody) {
        log.info("Sending PATCH request to: {}", url);
        log.debug("PATCH request body: {}", jsonBody);
        RequestOptions options = RequestOptions.create().setData(jsonBody);
        APIResponse response = requestContext.patch(url, options);
        logResponse(response);
        return response;
    }
    
    /**
     * Send an HTTP DELETE request
     * 
     * @param url The endpoint URL (relative to base URL)
     * @return APIResponse object containing the response
     */
    public APIResponse delete(String url) {
        log.info("Sending DELETE request to: {}", url);
        APIResponse response = requestContext.delete(url);
        logResponse(response);
        return response;
    }
    
    /**
     * Log the API response details
     * 
     * @param response The APIResponse object
     */
    private void logResponse(APIResponse response) {
        log.info("Response status: {} {}", response.status(), response.statusText());
        try {
            String responseBody = response.text();
            if (responseBody != null && !responseBody.isEmpty()) {
                if (responseBody.length() > 1000) {
                    log.info("Response body (truncated): {}", responseBody.substring(0, 1000) + "...");
                } else {
                    log.info("Response body: {}", responseBody);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to log response body: {}", e.getMessage());
        }
    }
    
    @Override
    public void close() {
        if (requestContext != null) {
            requestContext.dispose();
            log.debug("API request context disposed");
        }
        if (playwright != null) {
            playwright.close();
            log.debug("Playwright instance closed");
        }
    }
}
