package com.mytech.machinemonitorsystem.service;

import com.mytech.machinemonitorsystem.dto.FailedProductDto;
import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
import com.mytech.machinemonitorsystem.repository.FalseAlarmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Added this line to make stubbing lenient
public class FalseAlarmServiceTest {
    @Mock
    private FalseAlarmRepository falseAlarmRepository;
    @InjectMocks
    private FalseAlarmService falseAlarmService;
    private FalseAlarmMachineSummary falseAlarmMachineSummary;
    private List<FalseAlarmMachineSummary> falseAlarmMachineSummaryList;
    private int machineCode = 1;
    private int rackCode = 1;
    private int channelNumber = 1;

//    @BeforeEach
//    public void setup(){
//        falseAlarmMachineSummary =  new FalseAlarmMachineSummary();
//        falseAlarmMachineSummaryList =  new ArrayList<>();
//        falseAlarmMachineSummary.setMachineStationCode(1);
//        falseAlarmMachineSummary.setRackCode(2);
//        falseAlarmMachineSummary.setChannelNumber(3);
//        falseAlarmMachineSummary.setFalseAlarmCount(4);
//        falseAlarmMachineSummaryList.add(falseAlarmMachineSummary);
//    }
@BeforeEach
void setUp() {
    reset(falseAlarmRepository);

    // Create a spy of the actual service instance (FalseAlarmService)
    falseAlarmService = spy(new FalseAlarmService());
    // Manually inject the mocked repository into the spy
    // Note: @InjectMocks might not fully inject into a manually spied instance, so direct assignment is safer
    falseAlarmService.falseAlarmRepository = falseAlarmRepository;


    // Mock the behavior of the package-private helper method getAvailableRackCodeList on the SPY
    // The logic here matches the mocked logic provided in your FalseAlarmService
    doAnswer(invocation -> {
        List<Integer> list = invocation.getArgument(0);
        Integer machineCode = invocation.getArgument(1);
        list.clear(); // Clear existing elements before adding
        if (machineCode == 4) {
            list.add(104);
        }
        // If machineCode is not 4, list remains empty based on your mocked logic
        return null;
    }).when(falseAlarmService).getAvailableRackCodeList(any(List.class), any());

    // Mock the behavior of the package-private helper method getAvailableChannelList on the SPY
    // The logic here matches the mocked logic provided in your FalseAlarmService
    doAnswer(invocation -> {
        List<Integer> list = invocation.getArgument(0);
        // Integer rackCode = invocation.getArgument(1); // rackCode parameter is present but not used in your mock logic
        list.clear(); // Clear existing elements before adding
        list.addAll(List.of(1, 2)); // Your mocked data for availableChannelList
        return null;
    }).when(falseAlarmService).getAvailableChannelList(any(List.class), any());
}

    // --- Test Cases ---

    /**
     * Test case: All input parameters (machineCode, rackCode, channelNumber) are null or zero.
     * Verifies default behavior (machineCode defaults to 4, all available racks and channels are used
     * as per the mocked helper methods).
     */
    @Test
    void testGetFalseAlarmsForMachine_AllNullInputs() {
        // Given: All inputs are null
        Integer machineCode = null;
        Integer rackCode = null;
        Integer channelNumber = null;

        // Expected combinations based on your mocked helper methods:
        // machineCode defaults to 4.
        // getAvailableRackCodeList(list, 4) -> list contains [104]
        // getAvailableChannelList(list, any) -> list contains [1, 2]
        // Resulting combinations: [4, 104, 1], [4, 104, 2]

        // Mock repository responses for each expected combination
        when(falseAlarmRepository.findByMachineParameters(4, 104, 1))
                .thenReturn(Arrays.asList(new FalseAlarmMachineSummary(4, 104, 1, 10)));
        when(falseAlarmRepository.findByMachineParameters(4, 104, 2))
                .thenReturn(Arrays.asList(new FalseAlarmMachineSummary(4, 104, 2, 20)));

        // When
        List<FailedProductDto> result = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, channelNumber);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Expect 2 DTOs

        // Verify triggerStoredProcedure was called once
        verify(falseAlarmRepository, times(1)).triggerStoredProcedure();

        // Verify findByMachineParameters was called for each combination
        verify(falseAlarmRepository, times(1)).findByMachineParameters(4, 104, 1);
        verify(falseAlarmRepository, times(1)).findByMachineParameters(4, 104, 2);

        // Assert content of DTOs (order might vary due to HashSet, so check existence)
        assertTrue(result.contains(createDto(4, 104, 1, 200, Arrays.asList(10))));
        assertTrue(result.contains(createDto(4, 104, 2, 200, Arrays.asList(20))));
    }

    /**
     * Test case: Specific machineCode provided, rackCode and channelNumber are null/zero.
     * Verifies that only the specified rack for the machine and all default channels are used.
     * Based on your FalseAlarmService's mocked getAvailableRackCodeList, only machineCode=4 will add a rack.
     * For other machine codes, availableRackCodeList will be empty, leading to no combinations.
     */
    @Test
    void testGetFalseAlarmsForMachine_SpecificMachineOtherThanDefault() {
        // Given
        Integer machineCode = 5; // A machine code that doesn't add a rack in getAvailableRackCodeList
        Integer rackCode = null;
        Integer channelNumber = null;

        // Expected combinations based on your mocked helper methods:
        // machineCode is 5.
        // getAvailableRackCodeList(list, 5) -> list remains empty as per your mock logic.
        // If availableRackCodeList is empty, the combination loop will not run.

        // When
        List<FailedProductDto> result = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, channelNumber);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Expect an empty list as no combinations are formed

        // Verify triggerStoredProcedure was called once
        verify(falseAlarmRepository, times(1)).triggerStoredProcedure();
        // Verify findByMachineParameters was NOT called as no combinations were formed
        verify(falseAlarmRepository, times(0)).findByMachineParameters(anyInt(), anyInt(), anyInt());
    }


    /**
     * Test case: Specific machineCode and rackCode are provided, channelNumber is null/zero.
     * Verifies that only the specified rack is used, and all channels for that rack are used
     * as per the mocked helper method.
     */
    @Test
    void testGetFalseAlarmsForMachine_SpecificMachineAndRack() {
        // Given
        Integer machineCode = 4;
        Integer rackCode = 104; // Specific rack, matches your mock
        Integer channelNumber = null;

        // Expected combinations based on your mocked helper methods:
        // machineCode is 4, rackCode is 104.
        // availableRackCodeList will contain [104].
        // getAvailableChannelList(list, any) -> list contains [1, 2]
        // Resulting combinations: [4, 104, 1], [4, 104, 2]

        when(falseAlarmRepository.findByMachineParameters(4, 104, 1))
                .thenReturn(Arrays.asList(new FalseAlarmMachineSummary(4, 104, 1, 11)));
        when(falseAlarmRepository.findByMachineParameters(4, 104, 2))
                .thenReturn(Arrays.asList(new FalseAlarmMachineSummary(4, 104, 2, 22)));

        // When
        List<FailedProductDto> result = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, channelNumber);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(falseAlarmRepository, times(1)).triggerStoredProcedure();
        verify(falseAlarmRepository, times(1)).findByMachineParameters(4, 104, 1);
        verify(falseAlarmRepository, times(1)).findByMachineParameters(4, 104, 2);

        assertTrue(result.contains(createDto(4, 104, 1, 200, Arrays.asList(11))));
        assertTrue(result.contains(createDto(4, 104, 2, 200, Arrays.asList(22))));
    }

    /**
     * Test case: All input parameters (machineCode, rackCode, channelNumber) are specific.
     * Verifies that only the single specified combination is used.
     */
    @Test
    void testGetFalseAlarmsForMachine_AllSpecificInputs() {
        // Given
        Integer machineCode = 4;
        Integer rackCode = 104;
        Integer channelNumber = 1;

        // Expected combination: [4, 104, 1]

        when(falseAlarmRepository.findByMachineParameters(4, 104, 1))
                .thenReturn(Arrays.asList(new FalseAlarmMachineSummary(4, 104, 1, 123)));

        // When
        List<FailedProductDto> result = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, channelNumber);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(falseAlarmRepository, times(1)).triggerStoredProcedure();
        verify(falseAlarmRepository, times(1)).findByMachineParameters(4, 104, 1);

        FailedProductDto expectedDto = createDto(4, 104, 1, 200, Arrays.asList(123));
        assertEquals(expectedDto, result.get(0));
    }

    /**
     * Test case: The repository returns an empty list for a combination.
     * Verifies that a DTO is still created, but its failedProductCount list is empty.
     */
    @Test
    void testGetFalseAlarmsForMachine_RepositoryReturnsEmptyList() {
        // Given
        Integer machineCode = 4;
        Integer rackCode = 104;
        Integer channelNumber = 1;

        // Mock repository to return an empty list
        when(falseAlarmRepository.findByMachineParameters(4, 104, 1))
                .thenReturn(new ArrayList<>());

        // When
        List<FailedProductDto> result = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, channelNumber);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // Still one DTO created for the combination

        verify(falseAlarmRepository, times(1)).triggerStoredProcedure();
        verify(falseAlarmRepository, times(1)).findByMachineParameters(4, 104, 1);

        FailedProductDto dto = result.get(0);
        assertEquals(4, dto.getMachineId());
        assertEquals(104, dto.getRackId());
        assertEquals(1, dto.getChannelNumber());
        assertEquals(200, dto.getBatchSize());
        assertTrue(dto.getFailedProductCount().isEmpty()); // FailedProductCount should be empty
    }

    /**
     * Test case: The repository returns multiple FalseAlarmMachineSummary objects for one combination.
     * Verifies that all false alarm counts are aggregated into the FailedProductDto's list.
     */
    @Test
    void testGetFalseAlarmsForMachine_RepositoryReturnsMultipleSummaries() {
        // Given
        Integer machineCode = 4;
        Integer rackCode = 104;
        Integer channelNumber = 1;

        // Mock repository to return multiple summaries for one combination
        when(falseAlarmRepository.findByMachineParameters(4, 104, 1))
                .thenReturn(Arrays.asList(
                        new FalseAlarmMachineSummary(4, 104, 1, 5),
                        new FalseAlarmMachineSummary(4, 104, 1, 15),
                        new FalseAlarmMachineSummary(4, 104, 1, 25)
                ));

        // When
        List<FailedProductDto> result = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, channelNumber);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(falseAlarmRepository, times(1)).triggerStoredProcedure();
        verify(falseAlarmRepository, times(1)).findByMachineParameters(4, 104, 1);

        FailedProductDto dto = result.get(0);
        assertEquals(4, dto.getMachineId());
        assertEquals(104, dto.getRackId());
        assertEquals(1, dto.getChannelNumber());
        assertEquals(200, dto.getBatchSize());
        assertEquals(Arrays.asList(5, 15, 25), dto.getFailedProductCount()); // Should contain all counts
    }

    /**
     * Helper method to create a FailedProductDto for easier assertion.
     */
    private FailedProductDto createDto(Integer machineId, Integer rackId, Integer channelNumber, Integer batchSize, List<Integer> failedProductCount) {
        FailedProductDto dto = new FailedProductDto();
        dto.setMachineId(machineId);
        dto.setRackId(rackId);
        dto.setChannelNumber(channelNumber);
        dto.setBatchSize(batchSize);
        dto.setFailedProductCount(failedProductCount);
        return dto;
    }

//    @Test
//    public void testGetFalseAlarmsForMachineShouldThrowExceptionWithNegativeMachineCode(){
//        Assertions.assertThrows(IllegalArgumentException.class,()->{
//            falseAlarmService.getFalseAlarmsForMachine(-1);
//        },"Expect IllegalArgumentException but get nothing");
//    }
}
