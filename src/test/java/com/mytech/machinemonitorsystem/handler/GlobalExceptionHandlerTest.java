package com.mytech.machinemonitorsystem.handler;

import com.mytech.machinemonitorsystem.controller.DummyUnitTestController;
import com.mytech.machinemonitorsystem.controller.MachineMonitorController;
import com.mytech.machinemonitorsystem.service.FalseAlarmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({MachineMonitorController.class, DummyUnitTestController.class})
public class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    FalseAlarmService falseAlarmService;
    private static final String BASE_URL = "/api/v1/machines/status";
    private static final String DUMMY_ILLEGAL_ARGUMENT_URL = "/api/v1/dummy/illegal-argument";
    private static final String DUMMY_ERROR_URL = "/api/v1/dummy/error";
    // Test validation error (@Valid)
    @Test
    void whenPostEmptyJson_thenValidationFailed() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Validation failed for request"))
                .andExpect(jsonPath("$.details", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value(BASE_URL));
    }

    // Test malformed JSON
    @Test
    void whenPostMalformedJson_thenMalformedJson() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("MALFORMED_JSON"))
                .andExpect(jsonPath("$.message", containsString("Unexpected character")))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value(BASE_URL));
    }

    // Test parameter type mismatch (simulate sending string to long parameter)
    @Test
    void whenInvalidParameter_thenParameterTypeMismatch() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"machineId\":\"abc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("MALFORMED_JSON"))
                .andExpect(jsonPath("$.message", containsString("Cannot deserialize value of type `java.lang.Long`")))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value(BASE_URL));
    }

    // Test IllegalArgumentException
    @Test
    void whenIllegalArgumentException_thenIllegalArgument() throws Exception {
        mockMvc.perform(get(DUMMY_ILLEGAL_ARGUMENT_URL)) // simulate a controller throwing IllegalArgumentException
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ILLEGAL_ARGUMENT"))
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value(DUMMY_ILLEGAL_ARGUMENT_URL));
    }
    // Test resource not found
    @Test
    void whenEndpointNotFound_thenResourceNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("The requested API endpoint does not exist. Please check your URL."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/nonexistent"));
    }



    // Test generic internal exception
    @Test
    void whenUnexpectedException_thenInternalServerError() throws Exception {
        mockMvc.perform(get(DUMMY_ERROR_URL)) // simulate controller throwing RuntimeException
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value(DUMMY_ERROR_URL));
    }
}
