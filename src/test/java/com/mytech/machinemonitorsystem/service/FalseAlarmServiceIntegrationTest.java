/*
* All code commented, as it will trigger springboot to load dataSrouce, which is not available in springboot but in JBoss from now on.
* */

package com.mytech.machinemonitorsystem.service;

import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

//@DataJpaTest    //it can not autowire FalseAlarmService instance;
@SpringBootTest
@ActiveProfiles("local")// use "local" for local datasource
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)// Use real DB
public class FalseAlarmServiceIntegrationTest {

    @Autowired
    FalseAlarmService falseAlarmService;

    @Test
    public void debuggingGetFalseAlarmsForMachine(){
        falseAlarmService.getFalseAlarmsForMachine(0,0,0);
    }
}


/*
    * This test only pass under this circumstance:
    * 1. use current sample data which create 12 lines in the false_alarm_machine_summary table
    * *//*

//    @Test
//    public void testGetFalseAlarmsForMachineShouldPass(){
//        List<FalseAlarmMachineSummary> falseAlarmsForMachine = falseAlarmService.getFalseAlarmsForMachine(4);   //hard coded due to sample data
//        Assertions.assertTrue(falseAlarmsForMachine.size() == 12);
//    }
*/


