package com.playwrighttests.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for JSON processing
 */
public class JsonUtils {
    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    private JsonUtils() {
        // Prevent instantiation
    }
    
    /**
     * Convert an object to JSON string
     * 
     * @param obj the object to convert
     * @return JSON string
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
    
    /**
     * Convert JSON string to an object
     * 
     * @param <T> the type parameter
     * @param json the JSON string
     * @param classOfT the class of T
     * @return the object
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
    
    /**
     * Format JSON string with pretty printing
     * 
     * @param json the JSON string
     * @return formatted JSON string
     */
    public static String formatJson(String json) {
        try {
            JsonElement je = JsonParser.parseString(json);
            return gson.toJson(je);
        } catch (Exception e) {
            log.warn("Failed to format JSON: {}", e.getMessage());
            return json;
        }
    }
}
