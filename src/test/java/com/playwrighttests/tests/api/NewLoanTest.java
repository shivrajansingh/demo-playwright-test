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

public class NewLoanTest extends BaseApiTest {
    private static final Logger log = LoggerFactory.getLogger(NewLoanTest.class);
    @Test
    @DisplayName("test-002-api-create-loan")
    void testCreateLoan() {
        reportManager.addInfo("test-002-api-create-loan", "Creating a new loan");

        // Create payload data
        String payloadData = """
                                {
                  "clientTransactionId": null,
                  "totalAmount": -800.0,
                  "currency": "USD",
                  "transactionEmail": "Varsha.Anand@ezcorp.com",
                  "customer": {
                    "customerEzId": "0067125483"
                  },
                  "cartContext": {
                    "locationEzId": "01112",
                    "employeeEzId": "131313180",
                    "ipAddress": "10.251.21.45",
                    "manualOverride": {
                      "transactionDttm": "2025-02-12T09:05:00-0000",
                      "reasonCode": "TRANSACTION_FAILURE",
                      "manualImportEmployeeEzId": "131313149"
                    }
                  },
                  "transactions": [
                    {
                      "transactionType": "loan",
                      "transactionDetails": {
                        "amount": -800.0,
                        "loan": {
                            "ezId": "0098219",
                          "dueDate": "2025-06-29",
                          "finalDueDate": "2025-07-29",
                          "dateMade": "2025-05-29",
                          "financeCharge": 120.0,
                          "setupCharge": 0,
                          "additionalCharge": 0,
                          "customerImages": "cImafagagashgsagag",
                          "customerSbtVerified": true,
                          "customerOptInForLoanReminders": true,
                          "isMemberOfTheArmedForces": true,
                          "isDependentOfTheArmedForces": true,
                          "signature": "testSign",
                          "customerScore": "-1.909",
                          "customerMDMCategory": [
                            {
                              "categoryName": "Overall",
                              "parentCategoryName": "8764",
                              "pickupRate": "96.197174"
                            }
                          ],
                          "loanTerm": 30,
                          "flexLoanTerm": false,
                          "apr": 1.8,
                          "total": 920.0,
                          "selectedReasonCode": "GC",
                          "amountFinanced": 800.0,
                          "tax": 288.0,
                          "loanReqId": "c27c9cea-f51d-4bae-86f2-771788dfade8",
                          "outOfRangeApprover": "131313149",
                          "customerPickupRates": {
                            "jewelry": "a",
                            "gm": "b",
                            "firearm": "g",
                            "overall": "c"
                          },
                          "serviceCharge": 1.5,
                          "interestCharge": 0.2,
                          "policeFee": 0.0,
                          "cneChecked": false,
                          "lppChecked": false,
                          "hasJewelry": "F",
                          "oneDayLoan": false,
                          "items": [
                            {
                                            "itemStatus": "LN",
                                            "type": "MJ",
                                            "category": 724012,
                                            "locationEzId": "01112",
                                            "itemLocation": "01112",
                                            "origin": "LOAN",
                                            "originalTransId": "0118112",
                                            "itemGrade": "B",
                                            "loanAmount": 800,
                                            "amountFinanced": 800,
                                            "loanAppraisalDetails": {
                                                "originalOptimal": 225,
                                                "originalHigh": 276,
                                                "originalLow": 150,
                                                "originalEzSalesPrice": 500.99
                                            },
                                            "purchaseAppraisalDetails": {
                                                "originalOptimal": 150,
                                                "originalHigh": 201,
                                                "originalLow": 100,
                                                "originalEzSalesPrice": 500.99
                                            },
                                            "optimalAppraisal": 270,
                                            "highAppraisal": 331,
                                            "lowAppraisal": 180,
                                            "salesPrice": 600.99,
                                            "cost": 800,
                                            "itemReqId": "5764d9b2-ff48-4f6e-ac7d-bc01b1340570",
                                            "retailPrice": 1015.98,
                                            "description": "Ring Ladies",
                                            "attributes": [
                                                {
                                                    "type": "SL",
                                                    "name": "Color",
                                                    "value": "Black"
                                                },
                                                {
                                                    "type": "SL",
                                                    "name": "SCRAP",
                                                    "value": "YES"
                                                }
                                            ],
                                            "material": {
                                                "type": "GOLD",
                                                "quality": "8K_(.333)",
                                                "color": "ROSE_GOLD"
                                            },
                                            "weightInGrams": 11,
                                            "scrap": false,
                                            "adjItemCost": 0.0,
                                            "stones": [
                                                {
                                                    "size": "20",
                                                    "quality": "MEDIUM",
                                                    "shape": "ROUND",
                                                    "color": "WHITE",
                                                    "count": 2,
                                                    "weight": 10
                                                }
                                            ]
                                        }

                          ]
                        }
                      }
                    }
                  ],
                  "tenderIn": [],
                  "tenderOut": [
                    {
                      "tenderType": "CS",
                      "tenderAmount": 800.0
                    }
                  ]
                }
                                """;

        // Perform POST request
        APIResponse response = service.post("/cart/transactions/newloan", payloadData);
        log.info(response.text());
        // Validate response
        assertEquals(200, response.status(), "Status code should be 200");
        String responseText = response.text();
        log.info("Response text: {}", responseText);
        assertTrue(responseText.contains("clientTransactionId"), "Response should contain clientTransactionId field");

        //use direct assertions for clarity in debugging
        assertTrue(responseText.contains("\"locationEzId\""), "Response should contain locationEzId field");
        
        // Parse JSON to check the currency field directly
        JsonObject jsonObject = JsonParser.parseString(responseText).getAsJsonObject();
        assertTrue(jsonObject.has("payload"), "Response should have payload object");
        
        JsonObject payload = jsonObject.getAsJsonObject("payload");
        assertTrue(payload.has("currency"), "Payload should have currency field");
        
        String actualCurrency = payload.get("currency").getAsString();
        assertEquals("USD", actualCurrency, "Currency should be USD");
        
        reportManager.addSuccess("test-002-api-create-loan",
                "Successfully created loan with status " + response.status());
    }

}
