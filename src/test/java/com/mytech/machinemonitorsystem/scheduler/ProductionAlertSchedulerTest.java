package com.mytech.machinemonitorsystem.scheduler;

import com.mytech.machinemonitorsystem.dto.FailedProductDto;
import com.mytech.machinemonitorsystem.entity.MachineUnit;
import com.mytech.machinemonitorsystem.service.EmailService;
import com.mytech.machinemonitorsystem.service.FalseAlarmService;
import com.mytech.machinemonitorsystem.service.MachineLineService;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//@ActiveProfiles({"local"})
public class ProductionAlertSchedulerTest {

    @Mock
    EmailService emailService;
    @Mock
    FalseAlarmService falseAlarmService;
    @Mock
    MachineLineService machineLineService;
    @InjectMocks
    ProductionAlertScheduler productionAlertScheduler;

    private String[] defaultRecipients = {"jonathanwq.wang@gmail.com"};


    //prepare variables need
    private Set<MachineUnit> machineRackChannelCombinations = new HashSet<>();
    private List<FailedProductDto> failedProductDtosForMachineUnit1;// =new ArrayList<>();
    private List<FailedProductDto> failedProductDtosForMachineUnit2;// =new ArrayList<>();

    @BeforeEach
    void setup(){
        //mock the machine+rack+channels combination
        this.machineRackChannelCombinations = getMachineRackChannelCombinations();

        //mock the failed product dtos,make sure use the machine+rack+channels combination in getMachineRackChannelCombinations()
        this.failedProductDtosForMachineUnit1 = new ArrayList<>();
        FailedProductDto dto1 =  new FailedProductDto(5,105,3,200,List.of(3,2,0,0,0,6,7,8));
//        FailedProductDto dto2 =  new FailedProductDto(5,105,4,200,List.of(3,2,0,0,0,1,2,3));
        failedProductDtosForMachineUnit1.add(dto1);
//        failedProductDtosForMachineUnit1.add(dto2);

        this.failedProductDtosForMachineUnit2 = new ArrayList<>();
        FailedProductDto dto3 =  new FailedProductDto(5,106,4,200,List.of(3,2,0,0,0,1,2,3));
//        FailedProductDto dto4 =  new FailedProductDto(5,106,6,200,List.of(1,2,0,0,0,1,2,3));
        failedProductDtosForMachineUnit2.add(dto3);
//        failedProductDtosForMachineUnit2.add(dto4);

        //set the recipients for productionAlertScheduler
        productionAlertScheduler.setDefaultRecipients(this.defaultRecipients);

    }
    private Set<MachineUnit> getMachineRackChannelCombinations(){
        HashSet<MachineUnit> hashSet = new HashSet<>();
        MachineUnit unit1 = new MachineUnit();
        unit1.setMachineId(5);
        unit1.setRackId(105);
        unit1.setChannelNumber(3);
        MachineUnit unit2 = new MachineUnit();
        unit2.setMachineId(6);
        unit2.setRackId(106);
        unit2.setChannelNumber(4);
        hashSet.add(unit1);
        hashSet.add(unit2);
        return hashSet;
    }


    //test cases
    //given machineRackChannelCombinations normal data with count >=7,should send email ,
    @Test
    public void testMonitorFailedProductionSendsEmailWhenMachineRackChannelCombinationsIsCorrect(){
        //give
        Mockito.when(machineLineService.getMachineRackChannelCombinations())
                .thenReturn(machineRackChannelCombinations);
        Mockito.when(falseAlarmService.getFalseAlarmsForMachine(5,105, 3))
                .thenReturn(failedProductDtosForMachineUnit1);
        Mockito.when(falseAlarmService.getFalseAlarmsForMachine(6,106, 4))
                .thenReturn(failedProductDtosForMachineUnit2);
        //when
        productionAlertScheduler.monitorFailedProduction();

        //then assert
        String[] expectedTo = this.defaultRecipients;
        String ExpectedSubject = "Alert for Machine 5";
        String expectedTemplateName = "emails/alert-template";

        Context variables = new Context();
        variables.setVariable("mockedVariable","Thi is a mocked variable");
//        Mockito.spy(emailService).sendTemplatedHtmlEmail(to,subject,templateName,variables);
        ArgumentCaptor<Context> contextArgumentCaptor = ArgumentCaptor.forClass(Context.class);
        Mockito.verify(emailService).sendTemplatedHtmlEmail(
                eq(expectedTo),
                eq(ExpectedSubject),
                eq(expectedTemplateName),
                contextArgumentCaptor.capture()
        );
        //Asser the content of the email
        Context capturedValue = contextArgumentCaptor.getValue();
        Assertions.assertNotNull(capturedValue);
        String machineUnitListStr = (String)capturedValue.getVariable("machineUnitListStr");
        Assertions.assertTrue(machineUnitListStr.contains("Machine unit: 5-105-3, failed product amount in batch: 7 units.<br>"));
        LocalDateTime alertTimestamp = (LocalDateTime) capturedValue.getVariable("alertTimestamp");
        Assertions.assertNotNull(alertTimestamp);
//        // Assert the content of the email context
//        Context capturedContext = contextCaptor.getValue();
//        assertNotNull(capturedContext);
//        String machineUnitListStr = (String) capturedContext.getVariable("machineUnitListStr");
//        assertNotNull(machineUnitListStr);
//        assertTrue(machineUnitListStr.contains("Machine unit: 5-105-3, failed product amount in batch: 8 units.<br>"));
//        assertTrue(machineUnitListStr.contains("Machine unit: 6-106-4, failed product amount in batch: 7 units.<br>"));
//        assertNotNull(capturedContext.getVariable("alertTimestamp"));

    }
    //if machineRackChannelCombinations is null,machineLineService.getMachineRackChannelCombinations should not be called--not possible as it returns HashSet
    //if machineRackChannelCombinations is empty,machineLineService.getMachineRackChannelCombinations should not be called
    @Test
    public void testMonitorFailedProductionSendsEmailWhenMachineRackChannelCombinationsIsEmpty(){
        //given
        Mockito.when(machineLineService.getMachineRackChannelCombinations())
                .thenReturn(new HashSet<>());
        //when
        productionAlertScheduler.monitorFailedProduction();
        //then
//        Mockito.never(falseAlarmService.getFalseAlarmsForMachine());
        Mockito.verifyNoInteractions(falseAlarmService);
    }

    //if emailService has exception, should handle--we do not need it as emailService is designed to handle all exceptions.
/*    @Test
    public void testMonitorFailedProductionShouldHandleExcepitonWhenSendingEmail(){
        //given
//        Mockito.when(emailService.sendTemplatedHtmlEmail(any(),any(),any()))
//                .thenThrow(new MessagingException("mocked MessagingException"));
        Mockito.when(machineLineService.getMachineRackChannelCombinations())
                .thenReturn(machineRackChannelCombinations);
        Mockito.when(falseAlarmService.getFalseAlarmsForMachine(5,105, 3))
                .thenReturn(failedProductDtosForMachineUnit1);
        Mockito.when(falseAlarmService.getFalseAlarmsForMachine(6,106, 4))
                .thenReturn(failedProductDtosForMachineUnit2);
        Mockito.doThrow(new MessagingException("mocked MessagingException"))
                .when(emailService).sendTemplatedHtmlEmail(any(),any(),any(),any());
        // when
        productionAlertScheduler.monitorFailedProduction();
        //then emailService still try to send email
        Mockito.verify(emailService).sendTemplatedHtmlEmail(any(),any(),any(),any());
    }*/
}
