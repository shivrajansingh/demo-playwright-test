package com.playwrighttests.utils;

import com.microsoft.playwright.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Single service class for all API operations
 */
public class Service {
    private static final Logger log = LoggerFactory.getLogger(Service.class);
    private final ApiClient apiClient;
    
    /**
     * Create a new service with the default API client
     */
    public Service() {
        this.apiClient = new ApiClient();
    }
    
    /**
     * Create a new service with custom headers
     * 
     * @param headers Custom headers to include in all requests
     */
    public Service(Map<String, String> headers) {
        this.apiClient = new ApiClient(headers);
    }
    
    /**
     * Get the underlying API client
     * 
     * @return the API client
     */
    public ApiClient getApiClient() {
        return apiClient;
    }
    
    /**
     * Perform a GET request
     * 
     * @param endpoint the endpoint path
     * @return the API response
     */
    public APIResponse get(String endpoint) {
        log.info("GET request to: {}", endpoint);
        return apiClient.get(endpoint);
    }
    
    /**
     * Perform a GET request with query parameters
     * 
     * @param endpoint the endpoint path
     * @param queryParams map of query parameters
     * @return the API response
     */
    public APIResponse get(String endpoint, Map<String, String> queryParams) {
        log.info("GET request with params to: {}", endpoint);
        return apiClient.get(endpoint, queryParams);
    }
    
    /**
     * Perform a POST request with JSON body
     * 
     * @param endpoint the endpoint path
     * @param jsonBody the JSON payload as string
     * @return the API response
     */
    public APIResponse post(String endpoint, String jsonBody) {
        log.info("POST request to: {}", endpoint);
        return apiClient.post(endpoint, jsonBody);
    }
    
    /**
     * Perform a POST request with form data
     * 
     * @param endpoint the endpoint path
     * @param formData map of form fields
     * @return the API response
     */
    public APIResponse postForm(String endpoint, Map<String, String> formData) {
        log.info("POST form request to: {}", endpoint);
        return apiClient.postForm(endpoint, formData);
    }
    
    /**
     * Perform a PUT request with JSON body
     * 
     * @param endpoint the endpoint path
     * @param jsonBody the JSON payload as string
     * @return the API response
     */
    public APIResponse put(String endpoint, String jsonBody) {
        log.info("PUT request to: {}", endpoint);
        return apiClient.put(endpoint, jsonBody);
    }
    
    /**
     * Perform a PATCH request with JSON body
     * 
     * @param endpoint the endpoint path
     * @param jsonBody the JSON payload as string
     * @return the API response
     */
    public APIResponse patch(String endpoint, String jsonBody) {
        log.info("PATCH request to: {}", endpoint);
        return apiClient.patch(endpoint, jsonBody);
    }
    
    /**
     * Perform a DELETE request
     * 
     * @param endpoint the endpoint path
     * @return the API response
     */
    public APIResponse delete(String endpoint) {
        log.info("DELETE request to: {}", endpoint);
        return apiClient.delete(endpoint);
    }
    
    /**
     * Close the API client resources
     */
    public void close() {
        if (apiClient != null) {
            apiClient.close();
        }
    }
}
