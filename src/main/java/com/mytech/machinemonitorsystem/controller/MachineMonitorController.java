package com.mytech.machinemonitorsystem.controller;

import com.mytech.machinemonitorsystem.dto.FailedProductDto;
import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
import com.mytech.machinemonitorsystem.service.FalseAlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MachineMonitorController {

    @Autowired
    private FalseAlarmService falseAlarmService;

    @GetMapping("apis/v2/falseAlarm")
    public ResponseEntity<?> getFalseAlarms(
                                            @RequestParam @Nullable Integer machineCode,
                                            @RequestParam @Nullable Integer rackCode,
                                            @RequestParam @Nullable Integer channelNumber
                                            ){
        List<FailedProductDto> falseAlarmsForMachine = falseAlarmService.getFalseAlarmsForMachine(machineCode,rackCode,channelNumber);
        return ResponseEntity.ok()
                .body(falseAlarmsForMachine);
    }
}
