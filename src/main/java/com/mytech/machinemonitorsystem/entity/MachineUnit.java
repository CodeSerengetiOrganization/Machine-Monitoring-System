package com.mytech.machinemonitorsystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
//@NoArgsConstructor
public class MachineUnit {

    private long machineId;
    private long rackId;
    private long channelNumber;

    // --- Constructors ---
    public MachineUnit(long machineId, long rackId, long channelNumber) {
        this.machineId = machineId;
        this.rackId = rackId;
        this.channelNumber = channelNumber;
    }

    public MachineUnit() {}

    // --- Getters ---
    public long getMachineId() {
        return machineId;
    }

    public long getRackId() {
        return rackId;
    }

    public long getChannelNumber() {
        return channelNumber;
    }

    // --- Setters ---
    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }

    public void setRackId(long rackId) {
        this.rackId = rackId;
    }

    public void setChannelNumber(long channelNumber) {
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
        // Use Objects.hash for clarity (Java 7+)
        return java.util.Objects.hash(machineId, rackId, channelNumber);
    }
}
