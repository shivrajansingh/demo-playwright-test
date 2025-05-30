package com.playwrighttests.tests.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import com.playwrighttests.utils.ResponseAssert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;

public class RenewLoanTest extends BaseApiTest {

   
    private static final Logger log = LoggerFactory.getLogger(RenewLoanTest.class);
    @Test
    @DisplayName("test-003-api-renew-loan")
    void testCreateLoan() {
        reportManager.addInfo("test-003-api-renew-loan", "Creating a new loan");

        // Create payload data
        String payloadData = """
        {
    "clientTransactionId": null,
    "totalAmount": 3012.76,
    "currency": "USD",
    "transactionEmail": "Varsha.Anand@ezcorp.com",
    "customer": {
        "firstName": "abcd",
        "lastName": "efg",
        "customerEzId": "0063445049"
    },
    "cartContext": {
        "employeeEzId": "131313149",
        "ipAddress": "10.251.21.45",
        "locationEzId": "85915",
        "manualOverride": {
            "transactionDttm": "2025-03-10T11:30:00-0000",
            "reasonCode": "TRANSACTION_FAILURE",
            "manualImportEmployeeEzId": "131313180"
        }
    },
    "transactions": [
        {
            "transactionType": "loan_renewal",
            "transactionDetails": {
                "amount": 3000.00,
                "renewal": {
                    "itemUpdates": [
                        {
                            "itemEzId": "851330247165",
                            "updatedPrincipal": 0.00
                        }
                    ],
                    "approvalCode": "1234",
                    "updatedLoanAmount": 0.00,
                    "lostTicketFee": 12.76
                },
                "loan": {
                    "ezId": "3052665",
                    "location": {
                        "ezId": "85915"
                    },
                    "loanAmount": 1000.00
                }
            }
        }
    ],
    "tenderIn": [
        {
            "tenderType": "CS",
            "tenderAmount": 3012.76
        }
    ],
    "tenderOut": null
}

                                """;

        // Perform POST request
        APIResponse response = service.post("/cart/transactions/renewloan", payloadData);
        log.info(response.text());
        // First Check For Status Code 
        assertEquals(200, response.status(), "Status code should be 200");


        String responseText = response.text();
        assertTrue(responseText.contains("clientTransactionId"), "Response should contain clientTransactionId field");

        log.info("Check For Field customerEzId");
        ResponseAssert.assertContainsField(response, "customerEzId");
        
        
        ResponseAssert.assertContainsField(response, "locationEzId");

        log.info("Check for LocationEzId Value");

       Assertions.assertTrue(ResponseAssert.assertFieldEquals(responseText, "locationEzId", "85915"), 
                "Expected locationEzId to be 85915");


        log.info("Check for StateAlternateTextMX Value");
       Assertions.assertTrue(ResponseAssert.assertFieldEquals(responseText, "stateAlternateTextMX", "SI"));

        reportManager.addSuccess("test-002-api-create-loan",
                "Successfully created loan with status " + response.status());
    }

}
