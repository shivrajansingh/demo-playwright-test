package com.playwrighttests.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    public static String getFieldValue(String json, String fieldName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode targetField = root.findValue(fieldName);
            return targetField != null ? targetField.asText() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
