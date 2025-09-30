package com.mytech.machinemonitorsystem.controller;

import com.mytech.machinemonitorsystem.dto.FailedProductDto;
import com.mytech.machinemonitorsystem.service.FalseAlarmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MachineMonitorController {

/*    @Autowired
    private FalseAlarmService falseAlarmService;*/

    private final FalseAlarmService falseAlarmService;
    private static final Logger logger = LoggerFactory.getLogger(MachineMonitorController.class);

    public MachineMonitorController(FalseAlarmService falseAlarmService) {
        this.falseAlarmService = falseAlarmService;
    }

    @GetMapping("apis/v2/falseAlarm")
    public ResponseEntity<?> getFalseAlarms(
                                            @RequestParam @Nullable Long machineCode,
                                            @RequestParam @Nullable Long rackCode,
                                            @RequestParam @Nullable Long channelNumber
                                            ){
        logger.info("Received GET request for /apis/v2/falseAlarm,with parameters: apis/v2/falseAlarm:{},rackCode:{},channelNumber:{}",machineCode
                ,rackCode,channelNumber);
        List<FailedProductDto> falseAlarmsForMachine = falseAlarmService.getFalseAlarmsForMachine(machineCode,rackCode,channelNumber);
        logger.info("Successfully retrieved {} cases.",falseAlarmsForMachine.size());
        return ResponseEntity.ok()
                .body(falseAlarmsForMachine);
    }
}
