package com.mytech.machinemonitorsystem.dto;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class FailedProductDto {
    private Long machineId;  // for DTO, it is better to use Integer instead of int
    private Long rackId;
    private Long channelNumber;
    private Integer batchSize;
    private List<Long> failedProductCount;

// --- Constructors ---

    // No-argument constructor (often needed by frameworks like Spring, Jackson for deserialization)
    public FailedProductDto() {
    }

    // All-argument constructor (useful for creating instances with all values)
    public FailedProductDto(Long machineId, Long rackId, Long channelNumber, Integer batchSize, List<Long> failedProductCount) {
        this.machineId = machineId;
        this.rackId = rackId;
        this.channelNumber = channelNumber;
        this.batchSize = batchSize;
        this.failedProductCount = failedProductCount;
    }

    // --- Getters ---

    public Long getMachineId() {
        return machineId;
    }

    public Long getRackId() {
        return rackId;
    }

    public Long getChannelNumber() {
        return channelNumber;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public List<Long> getFailedProductCount() {
        return failedProductCount;
    }

    // --- Setters ---

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public void setRackId(Long rackId) {
        this.rackId = rackId;
    }

    public void setChannelNumber(Long channelNumber) {
        this.channelNumber = channelNumber;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public void setFailedProductCount(List<Long> failedProductCount) {
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
