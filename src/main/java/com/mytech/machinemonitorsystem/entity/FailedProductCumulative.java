package com.mytech.machinemonitorsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_product_fail_cumulative",
        indexes = {
                @Index(name = "idx_product_station_channel", columnList = "f_product_code, f_station_code, f_station_channel_no, f_product_seq")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_product_seq", columnNames = {"f_product_code", "f_product_seq"})
        })
public class FailedProductCumulative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    @Column(name = "f_product_code", nullable = false)
    private Long productCode;

    @NotNull
    @Positive
    @Column(name = "f_product_seq", nullable = false)
    private Long productSequence;

    @NotNull
    @PositiveOrZero
    @Column(name = "cumulative_fail_count", nullable = false)
    private Long cumulativeFailCount;

    @NotNull
    @Positive
    @Column(name = "f_station_code", nullable = false)
    private Long stationCode;

    @NotNull
    @Positive
    @Column(name = "f_station_channel_no", nullable = false)
    private Long stationChannelNumber;

    @NotNull
    @PastOrPresent
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductCode() {
        return productCode;
    }

    public void setProductCode(Long productCode) {
        this.productCode = productCode;
    }

    public Long getProductSequence() {
        return productSequence;
    }

    public void setProductSequence(Long productSequence) {
        this.productSequence = productSequence;
    }

    public Long getCumulativeFailCount() {
        return cumulativeFailCount;
    }

    public void setCumulativeFailCount(Long cumulativeFailCount) {
        this.cumulativeFailCount = cumulativeFailCount;
    }

    public Long getStationCode() {
        return stationCode;
    }

    public void setStationCode(Long stationCode) {
        this.stationCode = stationCode;
    }

    public Long getStationChannelNumber() {
        return stationChannelNumber;
    }

    public void setStationChannelNumber(Long stationChannelNumber) {
        this.stationChannelNumber = stationChannelNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}