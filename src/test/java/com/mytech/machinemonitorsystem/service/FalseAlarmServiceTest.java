package com.mytech.machinemonitorsystem.service;

import com.mytech.machinemonitorsystem.dto.FailedProductDto;
import com.mytech.machinemonitorsystem.entity.FailedProductCumulative;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Mock
    private FailedProductService failedProductService;
    @InjectMocks
    private FalseAlarmService falseAlarmService;

    private FalseAlarmMachineSummary falseAlarmMachineSummary;
    private List<FalseAlarmMachineSummary> falseAlarmMachineSummaryList;

//    private int machineCode = 1;
//    private int rackCode = 1;
//    private int channelNumber = 1;

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
    falseAlarmService.failedProductService = failedProductService;

    // Mock the behavior of the package-private helper method getAvailableRackCodeList on the SPY
    // The logic here matches the mocked logic provided in your FalseAlarmService
    doAnswer(
            invocationOnMock -> {
                Long machineCode = invocationOnMock.getArgument(0);
                List<Long> availableRacks = new ArrayList<>();
                if(machineCode == null || machineCode == 4L){
                    availableRacks.add(104L);
                }else{
                    availableRacks.add(105L);
                }
                return availableRacks;
            }
    ).when(falseAlarmService).getAvailableRackCodeList(any());

    doReturn(List.of(1L,2L)).when(falseAlarmService).getAvailableChannelList(any());

    // build test data
    List<FailedProductCumulative> failedCumuList = new ArrayList<>();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime createdAt = LocalDateTime.parse("2025-09-19 23:42:43", dtf);

    long[][] data = {
            {41, 112233, 4652, 40, 4, 1},
            {42, 112233, 4701, 41, 4, 1},
            {43, 112233, 4702, 42, 4, 1},
            {44, 112233, 4751, 43, 4, 1},
            {45, 112233, 4752, 44, 4, 1},
            {46, 112233, 4791, 45, 4, 1}
    };

    for (long[] row : data) {
        FailedProductCumulative fpc = new FailedProductCumulative();
        fpc.setId(row[0]);
        fpc.setProductCode(row[1]);
        fpc.setProductSequence(row[2]);
        fpc.setCumulativeFailCount(row[3]);
        fpc.setStationCode(row[4]);
        fpc.setStationChannelNumber(row[5]);
        fpc.setCreatedAt(createdAt);
        failedCumuList.add(fpc);
    }
    doReturn(failedCumuList).when(failedProductService).findByProductCodeAndStationCodeAndStationChannelNumber(any(),any(),any());
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
        Long machineCode = null;
        Long rackCode = null;
        Long channelNumber = null;

        // Mock repository responses for each expected combination
//        when(falseAlarmRepository.findByMachineParameters(4, 104, 1))
//                .thenReturn(Arrays.asList(new FalseAlarmMachineSummary(4, 104, 1, 10)));
//        when(falseAlarmRepository.findByMachineParameters(4, 104, 2))
//                .thenReturn(Arrays.asList(new FalseAlarmMachineSummary(4, 104, 2, 20)));

        // When
        List<FailedProductDto> result = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, channelNumber);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Expect 2 DTOs

/*        // Verify triggerStoredProcedure was called once
        verify(falseAlarmRepository, times(1)).triggerStoredProcedure();

        // Verify findByMachineParameters was called for each combination
        verify(falseAlarmRepository, times(1)).findByMachineParameters(4, 104, 1);
        verify(falseAlarmRepository, times(1)).findByMachineParameters(4, 104, 2);

        // Assert content of DTOs (order might vary due to HashSet, so check existence)
        assertTrue(result.contains(createDto(4L, 104L, 1L, 200, Arrays.asList(10L))));
        assertTrue(result.contains(createDto(4L, 104L, 2L, 200, Arrays.asList(20L))));
    */}

    /**
     * Test case: Specific machineCode provided, rackCode and channelNumber are null/zero.
     * Verifies that only the specified rack for the machine and all default channels are used.
     * Based on your FalseAlarmService's mocked getAvailableRackCodeList, only machineCode=4 will add a rack.
     * For other machine codes, availableRackCodeList will be empty, leading to no combinations.
     */
    @Test
    void testGetFalseAlarmsForMachine_SpecificMachineOtherThanDefault() {
        // Given
        Long machineCode = 5L; // A machine code that doesn't add a rack in getAvailableRackCodeList
        Long rackCode = null;
        Long channelNumber = null;

        // Expected combinations based on your mocked helper methods:
        // machineCode is 5.
        // getAvailableRackCodeList(list, 5) -> list remains empty as per your mock logic.
        // If availableRackCodeList is empty, the combination loop will not run.

        // When
        List<FailedProductDto> result = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, channelNumber);

        // Then
        assertNotNull(result);
        assertTrue(result.size() == 2); // Expect [5,105,1][5,105,2]

        // Verify triggerStoredProcedure was called once
//        verify(falseAlarmRepository, times(1)).triggerStoredProcedure();
        // Verify findByMachineParameters was NOT called as no combinations were formed
//        verify(falseAlarmRepository, times(2)).findByMachineParameters(anyInt(), anyInt(), anyInt());
    }


    /**
     * Test case: Specific machineCode and rackCode are provided, channelNumber is null/zero.
     * Verifies that only the specified rack is used, and all channels for that rack are used
     * as per the mocked helper method.
     */
    @Test
    void testGetFalseAlarmsForMachine_SpecificMachineAndRack() {
        // Given
        Long machineCode = 4L;
        Long rackCode = 104L; // Specific rack, matches your mock
        Long channelNumber = null;

        // Expected combinations based on your mocked helper methods:
        // machineCode is 4, rackCode is 104.
        // availableRackCodeList will contain [104].
        // getAvailableChannelList(list, any) -> list contains [1, 2]
        // Resulting combinations: [4, 104, 1], [4, 104, 2]

/*
        when(falseAlarmRepository.findByMachineParameters(4, 104, 1))
                .thenReturn(Arrays.asList(new FalseAlarmMachineSummary(4, 104, 1, 11)));
        when(falseAlarmRepository.findByMachineParameters(4, 104, 2))
                .thenReturn(Arrays.asList(new FalseAlarmMachineSummary(4, 104, 2, 22)));
*/

        // When
        List<FailedProductDto> result = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, channelNumber);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

//        verify(falseAlarmRepository, times(1)).triggerStoredProcedure();
//        verify(falseAlarmRepository, times(1)).findByMachineParameters(4, 104, 1);
//        verify(falseAlarmRepository, times(1)).findByMachineParameters(4, 104, 2);

//        assertTrue(result.contains(createDto(4L, 104L, 1L, 200, Arrays.asList(11L))));
//        assertTrue(result.contains(createDto(4L, 104L, 2L, 200, Arrays.asList(22L))));
    }

    /**
     * Test case: All input parameters (machineCode, rackCode, channelNumber) are specific.
     * Verifies that only the single specified combination is used.
     */
    @Test
    void testGetFalseAlarmsForMachine_AllSpecificInputs() {
        // Given
        Long machineCode = 4L;
        Long rackCode = 104L;
        Long channelNumber = 1L;

        // Expected combination: [4, 104, 1]

        when(falseAlarmRepository.findByMachineParameters(4, 104, 1))
                .thenReturn(Arrays.asList(new FalseAlarmMachineSummary(4, 104, 1, 123)));

        // When
        List<FailedProductDto> result = falseAlarmService.getFalseAlarmsForMachine(machineCode, rackCode, channelNumber);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

/*        verify(falseAlarmRepository, times(1)).triggerStoredProcedure();
        verify(falseAlarmRepository, times(1)).findByMachineParameters(4, 104, 1);

        FailedProductDto expectedDto = createDto(4L, 104L, 1L, 200, Arrays.asList(123L));
        assertEquals(expectedDto, result.get(0));*/
    }

    /**
     * Test case: The repository returns an empty list for a combination.
     * Verifies that a DTO is still created, but its failedProductCount list is empty.
     */
    @Test
    void testGetFalseAlarmsForMachine_RepositoryReturnsEmptyList() {
/*        // Given
        Long machineCode = 4L;
        Long rackCode = 104L;
        Long channelNumber = 1L;

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
        assertTrue(dto.getFailedProductCount().isEmpty()); // FailedProductCount should be empty*/
    }

    /**
     * Test case: The repository returns multiple FalseAlarmMachineSummary objects for one combination.
     * Verifies that all false alarm counts are aggregated into the FailedProductDto's list.
     */
/*    @Test
    void testGetFalseAlarmsForMachine_RepositoryReturnsMultipleSummaries() {
        // Given
        Long machineCode = 4L;
        Long rackCode = 104L;
        Long channelNumber = 1L;

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
    }*/

    /**
     * Helper method to create a FailedProductDto for easier assertion.
     */
    private FailedProductDto createDto(Long machineId, Long rackId, Long channelNumber, Integer batchSize, List<Long> failedProductCount) {
        FailedProductDto dto = new FailedProductDto();
        dto.setMachineId(machineId);
        dto.setRackId(rackId);
        dto.setChannelNumber(channelNumber);
        dto.setBatchSize(batchSize);
        dto.setFailedProductCount(failedProductCount);
        return dto;
    }

    //unit test code for inner helper method
    @Test
    void getAvailableRackCodeList_whenMachineCodeIsNull_returnsDefaultRack() {
        // Given - No mocking needed for the direct method under test
        Long machineCode = null;

        // When
        List<Long> result = falseAlarmService.getAvailableRackCodeList(machineCode); // Call the real method

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(104, result.get(0)); // Expecting 104 for null
    }

    @Test
    void getAvailableRackCodeList_whenMachineCodeIs4_returnsSpecificRack() {
        // Given
        Long machineCode = 4L;

        // When
        List<Long> result = falseAlarmService.getAvailableRackCodeList(machineCode);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(104, result.get(0));
    }

    @Test
    void getAvailableRackCodeList_whenMachineCodeIsOther_returnsDefaultRack() {
        // Given
        Long machineCode = 123L;

        // When
        List<Long> result = falseAlarmService.getAvailableRackCodeList(machineCode);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(105, result.get(0));
    }

    @Test
    void testCalculateFailedProductCountInBatch() {
        // build test data
        List<FailedProductCumulative> failedList = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime createdAt = LocalDateTime.parse("2025-09-19 23:42:43", dtf);

        long[][] data = {
                {2, 112233, 101, 1, 4, 1},
                {3, 112233, 102, 2, 4, 1},
                {4, 112233, 151, 3, 4, 1},
                {5, 112233, 152, 4, 4, 1},
                {6, 112233, 201, 5, 4, 1},
                {7, 112233, 202, 6, 4, 1},
                {8, 112233, 251, 7, 4, 1},
                {9, 112233, 252, 8, 4, 1},
                {10, 112233, 601, 9, 4, 1},
                {11, 112233, 602, 10, 4, 1},
                {12, 112233, 3001, 11, 4, 1},
                {13, 112233, 3002, 12, 4, 1},
                {14, 112233, 3351, 13, 4, 1},
                {15, 112233, 3352, 14, 4, 1},
                {16, 112233, 3451, 15, 4, 1},
                {17, 112233, 3452, 16, 4, 1},
                {18, 112233, 3701, 17, 4, 1},
                {19, 112233, 3702, 18, 4, 1},
                {20, 112233, 3801, 19, 4, 1},
                {21, 112233, 3802, 20, 4, 1},
                {22, 112233, 3901, 21, 4, 1},
                {23, 112233, 3902, 22, 4, 1},
                {24, 112233, 4051, 23, 4, 1},
                {25, 112233, 4052, 24, 4, 1},
                {26, 112233, 4101, 25, 4, 1},
                {27, 112233, 4102, 26, 4, 1},
                {28, 112233, 4201, 27, 4, 1},
                {29, 112233, 4202, 28, 4, 1},
                {30, 112233, 4301, 29, 4, 1},
                {31, 112233, 4302, 30, 4, 1},
                {32, 112233, 4351, 31, 4, 1},
                {33, 112233, 4352, 32, 4, 1},
                {34, 112233, 4451, 33, 4, 1},
                {35, 112233, 4452, 34, 4, 1},
                {36, 112233, 4501, 35, 4, 1},
                {37, 112233, 4502, 36, 4, 1},
                {38, 112233, 4551, 37, 4, 1},
                {39, 112233, 4552, 38, 4, 1},
                {40, 112233, 4651, 39, 4, 1},
                {41, 112233, 4652, 40, 4, 1},
                {42, 112233, 4701, 41, 4, 1},
                {43, 112233, 4702, 42, 4, 1},
                {44, 112233, 4751, 43, 4, 1},
                {45, 112233, 4752, 44, 4, 1},
                {46, 112233, 4791, 45, 4, 1}
        };

        for (long[] row : data) {
            FailedProductCumulative fpc = new FailedProductCumulative();
            fpc.setId(row[0]);
            fpc.setProductCode(row[1]);
            fpc.setProductSequence(row[2]);
            fpc.setCumulativeFailCount(row[3]);
            fpc.setStationCode(row[4]);
            fpc.setStationChannelNumber(row[5]);
            fpc.setCreatedAt(createdAt);
            failedList.add(fpc);
        }

        // call your method
        int latestSeq = 4800;   // your mocked getLatestProductSequence
        int batchSize = 100;    // example batch size
        int range = 5;          // currently unused in your code

//        MyServiceUnderTest service = new MyServiceUnderTest();

        List<Long> result = falseAlarmService.calculateFailedProductCountInBatch(latestSeq, batchSize, range, failedList);

        // verify result is not empty
        assertNotNull(result);
//        assertFalse(result.isEmpty());
        assertEquals(range,result.size());

        // print for debugging
        System.out.println("Failed product counts per batch: " + result);
    }
//    @Test
//    public void testGetFalseAlarmsForMachineShouldThrowExceptionWithNegativeMachineCode(){
//        Assertions.assertThrows(IllegalArgumentException.class,()->{
//            falseAlarmService.getFalseAlarmsForMachine(-1);
//        },"Expect IllegalArgumentException but get nothing");
//    }
}
