package com.mytech.machinemonitorsystem.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import com.mytech.machinemonitorsystem.dto.FailedProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class FalseAlarmServiceTest {

    @Mock
    private FailedProductService failedProductService;

    @InjectMocks
    private FalseAlarmService falseAlarmService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ============================
    // 1. Negative input tests
    // ============================
    @Test
    void testNegativeInputs_shouldThrowException() {
        // Negative machineCode
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () ->
                falseAlarmService.getFalseAlarmsForMachine(-1L, null, null));
        assertTrue(ex1.getMessage().contains("machineCode"));

        // Negative rackCode
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () ->
                falseAlarmService.getFalseAlarmsForMachine(1L, -5L, null));
        assertTrue(ex2.getMessage().contains("rackCode"));

        // Negative channelNumber
        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () ->
                falseAlarmService.getFalseAlarmsForMachine(1L, 2L, -3L));
        assertTrue(ex3.getMessage().contains("channelNumber"));
    }

    // ============================
    // 2. Default values tests
    // ============================
    @Test
    void testMachineCodeNullOrZero_shouldUseDefault4() {
        when(failedProductService.findByProductCodeAndStationCodeAndStationChannelNumber(anyLong(), anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        // machineCode = null
        List<FailedProductDto> result1 = falseAlarmService.getFalseAlarmsForMachine(null, 0L, 0L);
        for (FailedProductDto dto : result1) {
            assertEquals(4L, dto.getMachineId());
        }

        // machineCode = 0
        List<FailedProductDto> result2 = falseAlarmService.getFalseAlarmsForMachine(0L, 0L, 0L);
        for (FailedProductDto dto : result2) {
            assertEquals(4L, dto.getMachineId());
        }
    }

    @Test
    void testRackCodeNullOrZero_shouldUseAvailableRacks() {
        when(failedProductService.findByProductCodeAndStationCodeAndStationChannelNumber(anyLong(), anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        Long machineCode = 4L;

        // rackCode = null → getAvailableRackCodeList(4L) = [104L]
        List<FailedProductDto> result1 = falseAlarmService.getFalseAlarmsForMachine(machineCode, null, 1L);
        for (FailedProductDto dto : result1) {
            assertEquals(104L, dto.getRackId());
        }

        // rackCode = 0 → same as null
        List<FailedProductDto> result2 = falseAlarmService.getFalseAlarmsForMachine(machineCode, 0L, 1L);
        for (FailedProductDto dto : result2) {
            assertEquals(104L, dto.getRackId());
        }
    }

    @Test
    void testChannelNumberNullOrZero_shouldUseAvailableChannels() {
        when(failedProductService.findByProductCodeAndStationCodeAndStationChannelNumber(anyLong(), anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        Long machineCode = 4L;
        Long rackCode = 104L;

        // channelNumber = null → getAvailableChannelList(104L) = [1L,2L]
        List<FailedProductDto> result1 = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, null);
        assertEquals(2, result1.size());
        for (FailedProductDto dto : result1) {
            assertTrue(dto.getChannelNumber() == 1L || dto.getChannelNumber() == 2L);
        }

        // channelNumber = 0 → same as null
        List<FailedProductDto> result2 = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, 0L);
        assertEquals(2, result2.size());
        for (FailedProductDto dto : result2) {
            assertTrue(dto.getChannelNumber() == 1L || dto.getChannelNumber() == 2L);
        }
    }

    // ============================
    // 3. Specific valid inputs
    // ============================
    @Test
    void testSpecificInputs_shouldReturnSingleDto() {
        Long machineCode = 4L;
        Long rackCode = 104L;
        Long channelNumber = 1L;

        when(failedProductService.findByProductCodeAndStationCodeAndStationChannelNumber(anyLong(), eq(machineCode), eq(channelNumber)))
                .thenReturn(Collections.emptyList());

        List<FailedProductDto> result = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, channelNumber);

        assertEquals(1, result.size());
        FailedProductDto dto = result.get(0);
        assertEquals(machineCode, dto.getMachineId());
        assertEquals(rackCode, dto.getRackId());
        assertEquals(channelNumber, dto.getChannelNumber());
        assertEquals(200, dto.getBatchSize());
        assertNotNull(dto.getFailedProductCount());
    }

    // ============================
    // 4. Other machineCode branch (rack=105)
    // ============================
    @Test
    void testMachineOtherThan4_shouldUseRack105() {
        Long machineCode = 5L;

        when(failedProductService.findByProductCodeAndStationCodeAndStationChannelNumber(anyLong(), anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        List<FailedProductDto> result = falseAlarmService.getFalseAlarmsForMachine(machineCode, 0L, 0L);

        assertEquals(2, result.size()); // channels = [1,2]
        for (FailedProductDto dto : result) {
            assertEquals(machineCode, dto.getMachineId());
            assertEquals(105L, dto.getRackId());
        }
    }

    // ============================
    // 5. Empty failedProductService result
    // ============================
    @Test
    void testEmptyFailedProductService_returnsEmptyCounts() {
        Long machineCode = 4L;
        Long rackCode = 104L;
        Long channelNumber = 1L;

        when(failedProductService.findByProductCodeAndStationCodeAndStationChannelNumber(anyLong(), anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        List<FailedProductDto> result = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, channelNumber);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getFailedProductCount());
        assertTrue(result.get(0).getFailedProductCount().isEmpty());
    }
}
