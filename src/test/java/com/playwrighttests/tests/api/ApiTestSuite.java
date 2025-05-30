package com.playwrighttests.tests.api;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite that runs all API tests together
 */
@Suite
@SelectClasses({
    GetStoreTest.class, 
    NewLoanTest.class,
})
public class ApiTestSuite {
    // This class serves as a test suite container and doesn't need any code
}
