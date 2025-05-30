package com.playwrighttests.tests.api;

import com.microsoft.playwright.APIResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

public class GetStoreTest extends BaseApiTest {
    private static final Logger log = LoggerFactory.getLogger(GetStoreTest.class);

    @Test
    @DisplayName("test-001-api-get-store-receipts")
    void testGetStoreReceipts() {
        reportManager.addInfo("test-001-api-get-store-receipts", "Fetching Store Receipts");
        
        // Perform GET request
        APIResponse response = service.get("/receipt/options/domain/cart/store/09016");
        
        // Validate response
        assertEquals(200, response.status(), "Status code should be 200");
        assertTrue(response.text().contains("\"return_layaway\":"), "Response should contain return_layaway");
        String responseText = response.text();
        log.info("Response text: {}", responseText);
        reportManager.addSuccess("test-001-api-get-store-receipts", "Successfully retrieved Store Receipts with status " + response.status());
    }
}
