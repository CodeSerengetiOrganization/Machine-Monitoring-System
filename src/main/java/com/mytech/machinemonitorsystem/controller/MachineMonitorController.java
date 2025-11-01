package com.mytech.machinemonitorsystem.controller;

import com.mytech.model.v1.FailedProductDto;
import com.mytech.model.v1.MachineStatusRequest;
import com.mytech.model.v1.MachineStatusResponse;
import com.mytech.machinemonitorsystem.service.FalseAlarmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.OffsetDateTime;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Arrays;

@RestController
public class MachineMonitorController {

    private final FalseAlarmService falseAlarmService;
    private static final Logger logger = LoggerFactory.getLogger(MachineMonitorController.class);

    public MachineMonitorController(FalseAlarmService falseAlarmService){
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

    @PostMapping("/api/v1/machines/status")
    public ResponseEntity<MachineStatusResponse> getMachineStatus(@Valid @RequestBody MachineStatusRequest request) {
        logger.info("Received POST request for /api/v1/machines/status with machineId: {}, fixtureId: {}, channelNumber: {}",
                   request.getMachineId(), request.getFixtureId(), request.getChannelNumber());

        // Build a mocked MachineStatusResponse using the local DTO constructor
/*
        Long machineId = request.getMachineId();
        Long rackId = (request.getFixtureId() != null) ? request.getFixtureId().longValue() : 1L;
        Long channelNumber = (request.getChannelNumber() != null) ? request.getChannelNumber() : 1L;

        Integer batchSize = 100;
        if (request.getFilters() != null && request.getFilters().getBatchSize() != null) {
            batchSize = request.getFilters().getBatchSize();
        }

        List<Integer> failedProductCount = Arrays.asList(5, 2, 7); // mocked counts per batch
        OffsetDateTime timestamp = OffsetDateTime.now();

        MachineStatusResponse response = new MachineStatusResponse(machineId, rackId, channelNumber, batchSize, failedProductCount, timestamp);
*/
        Long machineId = request.getMachineId();
        Long rackId = (request.getFixtureId() != null) ? request.getFixtureId().longValue() : 1L;
        Long channelNumber = (request.getChannelNumber() != null) ? request.getChannelNumber() : 1L;

        List<FailedProductDto> failedProductDtos = falseAlarmService.getFalseAlarmsForMachine(machineId, rackId, channelNumber);
        MachineStatusResponse response = new MachineStatusResponse(failedProductDtos);
        logger.info("Successfully retrieved machine status for machineId: {}", machineId);
        return ResponseEntity.ok()
                .body(response);
    }

}
