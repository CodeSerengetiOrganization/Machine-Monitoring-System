package com.mytech.machinemonitorsystem.handler;

import com.mytech.machinemonitorsystem.controller.MachineMonitorController;
import com.mytech.machinemonitorsystem.service.FalseAlarmService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileNotFoundException;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MachineMonitorController.class)
public class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    FalseAlarmService falseAlarmService;
    private String correctUrl = "/apis/v2/falseAlarm";

    @Test
    public void shouldHandleMethodArgumentTypeMismatchException() throws Exception {
        //directly use mockMvc to trigger
        mockMvc.perform(get(correctUrl)
                    .param("machineCode", "invalidMachineCode")
                    .param("rackCode","104")
                    .param("channelNumber","1")
                    .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("PARAMETER_TYPE_MISMATCH"))
                .andExpect(jsonPath("$.message").value(containsString("Invalid value invalidMachineCode provided for parameter machineCode. Expected type: Integer")));
    }
    @Test
    public void shouldHandleNoHandlerFoundExceptionWhenUrlIncorrect() throws Exception {
        String incorrectUrl = "/apis/v2/incorrectUrl";
        mockMvc.perform(get(incorrectUrl)
                .param("machineCode", "4")
                .param("rackCode","104")
                .param("channelNumber","1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("The requested API endpoint does not exist. Please check your URL."));
//                );
    }
    @Test
    public void shouldHandleUnexpectedExceptionWhenNoSpecificHandlerForIt() throws Exception {
        //arrange--mock an exception that never handled in GlobalExceptionHandler
        IllegalStateException mockedUnexpectedException = new IllegalStateException("mocked unexpected exception.");
        Mockito.doThrow(mockedUnexpectedException).when(falseAlarmService).getFalseAlarmsForMachine(any(),any(),any());
        //action&assert
        mockMvc.perform(get(correctUrl)
                .param("machineCode", "4")
                .param("rackCode","104")
                .param("channelNumber","1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.statusCode").value("500"))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."));
    }
}
