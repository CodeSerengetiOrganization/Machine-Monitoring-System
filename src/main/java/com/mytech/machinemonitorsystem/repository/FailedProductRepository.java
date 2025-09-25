package com.mytech.machinemonitorsystem.repository;

import com.mytech.machinemonitorsystem.entity.FailedProductCumulative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FailedProductRepository extends JpaRepository<FailedProductCumulative, Long> {
// Find all records by product code
List<FailedProductCumulative> findByProductCode(Long productCode);

// Find all records by station code
//List<FailedProductCumulative> findByStationCode(Long stationCode);

// Find a record by product code and sequence
//Optional<FailedProductCumulative> findByProductCodeAndProductSequence(Long productCode, Long productSequence);

// Find all records created after a specific date
List<FailedProductCumulative> findByCreatedAtAfter(LocalDateTime createdAt);

// Custom query to find cumulative failures for a specific station
//@Query("SELECT f FROM FailedProductCumulative f WHERE f.stationCode = :stationCode AND f.cumulativeFailCount > :failCount")
List<FailedProductCumulative> findFailuresByStationCode( Long stationCode);

// Method to find records by product code, station code, and station channel number
List<FailedProductCumulative> findByProductCodeAndStationCodeAndStationChannelNumber(Long productCode, Long stationCode, Long stationChannelNumber);

// Method to find records by station code and station channel number
List<FailedProductCumulative> findByStationCodeAndStationChannelNumber(Long stationCode, Long stationChannelNumber);
}
