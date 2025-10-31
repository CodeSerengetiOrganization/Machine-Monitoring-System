package com.mytech.machinemonitorsystem.handler;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class GlobalExceptionHandlerIntegrationTest {
    // Test case 1: Invalid JSON syntax
    public void testInvalidJsonSyntax() throws Exception {
        String invalidJson = """
            {
              "machineId": 1001,
              "fixtureId": 22
              // missing closing brace and comma
            """;

        // Without HttpMessageNotReadableException handler:
        // - This would be caught by Exception.class handler
        // - Would return 500 Internal Server Error
        // - Client gets wrong status code

        // With HttpMessageNotReadableException handler:
        // - Returns 400 Bad Request (correct)
        // - Returns meaningful error message
        // - Follows API contract
    }

    // Test case 2: Wrong data types
    public void testWrongDataTypes() throws Exception {
        String wrongTypeJson = """
            {
              "machineId": "this-should-be-a-number",
              "fixtureId": 22,
              "dateRange": {
                "startDate": "not-a-valid-date-format"
              }
            }
            """;

        // This JSON is syntactically valid but has wrong data types
        // Jackson cannot deserialize it to MachineStatusRequest
        // Throws HttpMessageNotReadableException
    }

    // Test case 3: Completely invalid content
    public void testCompletelyInvalidContent() throws Exception {
        String notJson = "This is not JSON at all";

        // Client sends non-JSON content with application/json header
        // Should return 400 Bad Request, not 500 Internal Server Error
    }
}
