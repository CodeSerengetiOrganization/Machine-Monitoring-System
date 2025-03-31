package com.mytech.machinemonitorsystem.repository;

import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FalseAlarmRepository extends JpaRepository<FalseAlarmMachineSummary,Integer > {
    @Query(value = "SELECT f FROM FalseAlarmMachineSummary f WHERE f.machineStationCode = :machineStationCode",
            countQuery = "SELECT count(f) FROM FalseAlarmMachineSummary f WHERE f.machineStationCode = :machineStationCode")
    List<FalseAlarmMachineSummary> findByMachineStationCode(@Param("machineStationCode") int machineStationCode);
}
