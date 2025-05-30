package com.mytech.machinemonitorsystem.dto;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class FailedProductDto {
    private Integer machineId;  // for DTO, it is better to use Integer instead of int
    private Integer rackId;
    private Integer channelNumber;
    private Integer batchSize;
    private List<Integer> failedProductCount;

// --- Constructors ---

    // No-argument constructor (often needed by frameworks like Spring, Jackson for deserialization)
    public FailedProductDto() {
    }

    // All-argument constructor (useful for creating instances with all values)
    public FailedProductDto(Integer machineId, Integer rackId, Integer channelNumber, Integer batchSize, List<Integer> failedProductCount) {
        this.machineId = machineId;
        this.rackId = rackId;
        this.channelNumber = channelNumber;
        this.batchSize = batchSize;
        this.failedProductCount = failedProductCount;
    }

    // --- Getters ---

    public Integer getMachineId() {
        return machineId;
    }

    public Integer getRackId() {
        return rackId;
    }

    public Integer getChannelNumber() {
        return channelNumber;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public List<Integer> getFailedProductCount() {
        return failedProductCount;
    }

    // --- Setters ---

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public void setRackId(Integer rackId) {
        this.rackId = rackId;
    }

    public void setChannelNumber(Integer channelNumber) {
        this.channelNumber = channelNumber;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public void setFailedProductCount(List<Integer> failedProductCount) {
        this.failedProductCount = failedProductCount;
    }

    // --- Overridden Methods ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailedProductDto that = (FailedProductDto) o;
        return Objects.equals(machineId, that.machineId) &&
                Objects.equals(rackId, that.rackId) &&
                Objects.equals(channelNumber, that.channelNumber) &&
                Objects.equals(batchSize, that.batchSize) &&
                Objects.equals(failedProductCount, that.failedProductCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(machineId, rackId, channelNumber, batchSize, failedProductCount);
    }

    @Override
    public String toString() {
        return "FailedProductDto{" +
                "machineId=" + machineId +
                ", rackId=" + rackId +
                ", channelNumber=" + channelNumber +
                ", batchSize=" + batchSize +
                ", failedProductCount=" + failedProductCount +
                '}';
    }
}
