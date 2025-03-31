package com.mytech.machinemonitorsystem.controller;

import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
import com.mytech.machinemonitorsystem.service.FalseAlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MachineMonitorController {

    @Autowired
    private FalseAlarmService falseAlarmService;

    @GetMapping("apis/falseAlarm/{machineCode}")
    public ResponseEntity<?> getFalseAlarms(@PathVariable int machineCode){
        List<FalseAlarmMachineSummary> falseAlarmsForMachine = falseAlarmService.getFalseAlarmsForMachine(machineCode);
        return ResponseEntity.ok()
                .body(falseAlarmsForMachine);
    }
}
