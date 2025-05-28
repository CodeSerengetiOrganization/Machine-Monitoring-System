package com.mytech.machinemonitorsystem.service;

import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
import com.mytech.machinemonitorsystem.repository.FalseAlarmRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FalseAlarmService {
    @Autowired
    FalseAlarmRepository falseAlarmRepository;

    /**
     * Retrieves a list of false alarm summaries(contains the failed part data) for a specific machine, rack, and channel.
     * This method is transactional to ensure consistency during the data retrieval process.
     *
     * @param machineCode   the unique code identifying the machine
     * @param rackCode      the code identifying the rack which is compatible with the machine
     * @param channelNumber the number identifying the specific channel of the rack to query
     * @return a list of {@link FalseAlarmMachineSummary} objects representing the false alarms
     */
    @Transactional
    public List<FalseAlarmMachineSummary> getFalseAlarmsForMachine(int machineCode,int rackCode,int channelNumber){
        if(machineCode < 0){
            throw new IllegalArgumentException("machineCode should be greater than or equal to 0. Current:"+machineCode);
        }
        falseAlarmRepository.triggerStoredProcedure();
        List<FalseAlarmMachineSummary> falseAlarms = falseAlarmRepository.findByMachineParameters(machineCode,rackCode,channelNumber);
        return falseAlarms;
    }
}
