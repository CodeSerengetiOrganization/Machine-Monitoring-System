package com.mytech.machinemonitorsystem.service;

import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
import com.mytech.machinemonitorsystem.repository.FalseAlarmRepository;
import com.mytech.machinemonitorsystem.service.FalseAlarmService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class FalseAlarmServiceTest {
    @Mock
    FalseAlarmRepository falseAlarmRepository;

    @InjectMocks
    FalseAlarmService falseAlarmService;

    private FalseAlarmMachineSummary falseAlarmMachineSummary;
    private List<FalseAlarmMachineSummary> falseAlarmMachineSummaryList;

    @BeforeEach
    public void setup(){
        falseAlarmMachineSummary =  new FalseAlarmMachineSummary();
        falseAlarmMachineSummaryList =  new ArrayList<>();
        falseAlarmMachineSummary.setMachineStationCode(1);
        falseAlarmMachineSummary.setRackCode(2);
        falseAlarmMachineSummary.setChannelNumber(3);
        falseAlarmMachineSummary.setFalseAlarmCount(4);
        falseAlarmMachineSummaryList.add(falseAlarmMachineSummary);
    }

    @Test
    public void testGetFalseAlarmsForMachineShouldPass(){
        Mockito.doNothing()
                .when(falseAlarmRepository).triggerStoredProcedure();
        Mockito.doReturn(falseAlarmMachineSummaryList)
                .when(falseAlarmRepository).findByMachineStationCode(Mockito.anyInt());
        List<FalseAlarmMachineSummary> falseAlarmsForMachine = falseAlarmService.getFalseAlarmsForMachine(1);
        Assertions.assertTrue(falseAlarmsForMachine.size() == 1);
    }

    @Test
    public void testGetFalseAlarmsForMachineShouldThrowExceptionWithNegativeMachineCode(){
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            falseAlarmService.getFalseAlarmsForMachine(-1);
        },"Expect IllegalArgumentException but get nothing");
    }
}
