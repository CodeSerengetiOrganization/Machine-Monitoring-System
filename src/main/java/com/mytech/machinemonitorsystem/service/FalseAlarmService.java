package com.mytech.machinemonitorsystem.service;

import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
import com.mytech.machinemonitorsystem.repository.FalseAlarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FalseAlarmService {
    @Autowired
    FalseAlarmRepository falseAlarmRepository;

    public List<FalseAlarmMachineSummary> getFalseAlarmsForMachine(int machineCode){
        if(machineCode < 0){
            throw new IllegalArgumentException("machineCode should be greater than or equal to 0. Current:"+machineCode);
        }
        List<FalseAlarmMachineSummary> falseAlarms = falseAlarmRepository.findByMachineStationCode(machineCode);
        return falseAlarms;
    }
}
