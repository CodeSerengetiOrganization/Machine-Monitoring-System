package com.mytech.machinemonitorsystem.service;

import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
import com.mytech.machinemonitorsystem.repository.FalseAlarmRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        List<Integer> availableRackCodeList = new ArrayList<>();
        List<Integer> availableChannelList = new ArrayList<>();
        Set<List<Integer>> combinations =  new HashSet<List<Integer>>();
        //1. pre-check of input parameters
        if(machineCode < 0){
            throw new IllegalArgumentException("machineCode should be greater than or equal to 0. Current:"+machineCode);
        }
        if(rackCode < 0){
            throw new IllegalArgumentException("rackCode should be greater than or equal to 0. Current:"+rackCode);
        }
        if(channelNumber < 0){
            throw new IllegalArgumentException("channelNumber should be greater than or equal to 0. Current:"+channelNumber);
        }
        if(machineCode ==0){
            //need a default machine id, should use all compatible rackCode, should use all available channel numbers
            machineCode = 4;
        }

        if(rackCode == 0){
            //use all available racks
            getAvailableRackCodeList(availableRackCodeList,machineCode);
        }else{
            //use current existing rack code
            availableRackCodeList.add(rackCode);
        }
        System.out.println("availableRackCodeList:"+availableRackCodeList.toString());

        if(channelNumber == 0){
            getAvailableChannelList(availableChannelList,rackCode);
        }else{
            availableChannelList.add(channelNumber);
        }
        System.out.println("availableChannelList:"+availableChannelList.toString());

        //build the combination set
        for(Integer rack:availableRackCodeList){
            for (Integer channel: availableChannelList){
                combinations.add(List.of(machineCode,rack,channel));
            }
        }
        //for debugging, print the combination
        System.out.println("build the combinations:" +combinations.toString());
//        return null;

        List<FalseAlarmMachineSummary> falseAlarms = new ArrayList<>();
        falseAlarmRepository.triggerStoredProcedure();
        for(List<Integer>  combination : combinations){
            System.out.println("combination:");
            for(int i=0;i<combination.size();i++){
                System.out.println(combination.get(i));
            }

            List<FalseAlarmMachineSummary> falseAlarmData = falseAlarmRepository.findByMachineParameters(combination.get(0),combination.get(1),combination.get(2));
            System.out.println("falseAlarmData in loop:"+falseAlarmData.toString());
            falseAlarms.addAll(falseAlarmData);
        }
        System.out.println("falseAlarms:"+falseAlarms.toString());
        return falseAlarms;
    }

    /**
     * Get all available channel number that is on the rack
     * @param availableChannelList the list to add channel number code
     * @param rackCode rack Id
     * @return the modified available channel numbers that are on the provided machineCode
     * */
    private void getAvailableChannelList(List<Integer> availableChannelList, int rackCode) {
        availableChannelList.addAll(List.of(1,2)); //mocked data
    }

    /**
    * Get all available rack that is compatible with provided
    * @param availableRackCodeList the list to add rack code
    * @param machineCode machine Id
     * @return the modified available rack code that are compatible with provided machineCode
     * */
    private void getAvailableRackCodeList(List<Integer> availableRackCodeList,int machineCode) {
        //mocked logic
        if(machineCode == 4){
            availableRackCodeList.add(104);
        }
    }
}
