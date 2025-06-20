package com.mytech.machinemonitorsystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
//@NoArgsConstructor
public class MachineUnit {

    private int machineId;
    private int rackId;
    private int channelNumber;
    // --- Constructor
    public MachineUnit(int machineId, int rackId, int channelNumber) {
        this.machineId = machineId;
        this.rackId = rackId;
        this.channelNumber = channelNumber;
    }
    public MachineUnit(){}

    // --- Getters ---
    public int getMachineId() {
        return machineId;
    }

    public int getRackId() {
        return rackId;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    // --- Setters ---
    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public void setRackId(int rackId) {
        this.rackId = rackId;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    // --- toString() ---
    @Override
    public String toString() {
        return "MachineUnit{" +
                "machineId=" + machineId +
                ", rackId=" + rackId +
                ", channelNumber=" + channelNumber +
                '}';
    }

    // --- equals() and hashCode() ---
    // These two methods should always be overridden together if you override one.
    // They are crucial for correct behavior in collections like HashMap or HashSet.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MachineUnit that = (MachineUnit) o;
        return machineId == that.machineId &&
                rackId == that.rackId &&
                channelNumber == that.channelNumber;
    }

    @Override
    public int hashCode() {
        // A common way to generate hash code using relevant fields
        int result = machineId;
        result = 31 * result + rackId;
        result = 31 * result + channelNumber;
        return result;
        // Or using Java's Objects.hash() (Java 7+) for conciseness:
        // return Objects.hash(machineId, rackId, channelNumber);
    }

    //    private Map<Integer, String > machineIdNameMap;
//    private Map<Integer, String > rackIdNameMap;
    //    private String machineName;
    //    private String rackName;
}
