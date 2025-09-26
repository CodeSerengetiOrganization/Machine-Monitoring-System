package com.mytech.machinemonitorsystem.service;

import com.mytech.machinemonitorsystem.entity.FailedProductCumulative;
import com.mytech.machinemonitorsystem.repository.FailedProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FailedProductService {
    private final FailedProductRepository failedProductRepository;

    @Autowired
    public FailedProductService(FailedProductRepository failedProductRepository) {
        this.failedProductRepository = failedProductRepository;
    }

    public List<FailedProductCumulative> findByProductCode(Long productCode) {
        return failedProductRepository.findByProductCode(productCode);
    }

    public List<FailedProductCumulative> findByCreatedAtAfter(LocalDateTime createdAt) {
        return failedProductRepository.findByCreatedAtAfter(createdAt);
    }

    public List<FailedProductCumulative> findFailuresByStationCode(Long stationCode) {
        return failedProductRepository.findFailuresByStationCode(stationCode);
    }

    public List<FailedProductCumulative> findByProductCodeAndStationCodeAndStationChannelNumber(Long productCode, Long stationCode, Long stationChannelNumber) {
        return failedProductRepository.findByProductCodeAndStationCodeAndStationChannelNumber(productCode, stationCode, stationChannelNumber);
    }

    public List<FailedProductCumulative> findByStationCodeAndStationChannelNumber(Long stationCode, Long stationChannelNumber) {
        return failedProductRepository.findByStationCodeAndStationChannelNumber(stationCode, stationChannelNumber);
    }
}
