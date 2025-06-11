package com.mytech.machinemonitorsystem.service;

import com.mytech.machinemonitorsystem.entity.MachineUnit;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/*
* This is a service that provide method related to all machine unites in the manufacturing line.
* */
@Service
public class MachineLineService {
    /*
    * get the combinations of machineId,rackId and channelNumber;
    * normally need to query from MES database,currently using mock data
    * */
    public Set<MachineUnit> getMachineRackChannelCombinations(){
        HashSet<MachineUnit> hashSet = new HashSet<>();
        MachineUnit unit1 = new MachineUnit();
        unit1.setMachineId(4);
        unit1.setRackId(104);
        unit1.setChannelNumber(1);
        MachineUnit unit2 = new MachineUnit();
        unit2.setMachineId(4);
        unit2.setRackId(104);
        unit2.setChannelNumber(2);
        hashSet.add(unit1);
        hashSet.add(unit2);
        return hashSet;
    }
}
