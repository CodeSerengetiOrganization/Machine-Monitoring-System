package com.mytech.machinemonitorsystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "false_alarm_machine_summary")
public class FalseAlarmMachineSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "machine_station_code", nullable = false)
    private Integer machineStationCode;

    @Column(name = "rack_code", nullable = false)
    private Integer rackCode;

    @Column(name = "channel_number", nullable = false)
    private Integer channelNumber;

    @Column(name = "false_alarm_count", nullable = false)
    private Integer falseAlarmCount;

    // Constructors (default and parameterized)

    public FalseAlarmMachineSummary() {
    }

    public FalseAlarmMachineSummary(Integer machineStationCode, Integer rackCode, Integer channelNumber, Integer falseAlarmCount) {
        this.machineStationCode = machineStationCode;
        this.rackCode = rackCode;
        this.channelNumber = channelNumber;
        this.falseAlarmCount = falseAlarmCount;
    }

    // Getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMachineStationCode() {
        return machineStationCode;
    }

    public void setMachineStationCode(Integer machineStationCode) {
        this.machineStationCode = machineStationCode;
    }

    public Integer getRackCode() {
        return rackCode;
    }

    public void setRackCode(Integer rackCode) {
        this.rackCode = rackCode;
    }

    public Integer getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(Integer channelNumber) {
        this.channelNumber = channelNumber;
    }

    public Integer getFalseAlarmCount() {
        return falseAlarmCount;
    }

    public void setFalseAlarmCount(Integer falseAlarmCount) {
        this.falseAlarmCount = falseAlarmCount;
    }

    // toString(), equals(), hashCode() (optional, but recommended)

    @Override
    public String toString() {
        return "FalseAlarmMachineSummary{" +
                "id=" + id +
                ", machineStationCode=" + machineStationCode +
                ", rackCode=" + rackCode +
                ", channelNumber=" + channelNumber +
                ", falseAlarmCount=" + falseAlarmCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FalseAlarmMachineSummary that = (FalseAlarmMachineSummary) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (machineStationCode != null ? !machineStationCode.equals(that.machineStationCode) : that.machineStationCode != null)
            return false;
        if (rackCode != null ? !rackCode.equals(that.rackCode) : that.rackCode != null) return false;
        if (channelNumber != null ? !channelNumber.equals(that.channelNumber) : that.channelNumber != null) return false;
        return falseAlarmCount != null ? falseAlarmCount.equals(that.falseAlarmCount) : that.falseAlarmCount == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (machineStationCode != null ? machineStationCode.hashCode() : 0);
        result = 31 * result + (rackCode != null ? rackCode.hashCode() : 0);
        result = 31 * result + (channelNumber != null ? channelNumber.hashCode() : 0);
        result = 31 * result + (falseAlarmCount != null ? falseAlarmCount.hashCode() : 0);
        return result;
    }
}