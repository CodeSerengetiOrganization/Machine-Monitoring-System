package com.mytech.machinemonitorsystem.repository;

import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FalseAlarmRepository extends JpaRepository<FalseAlarmMachineSummary,Integer > {
    @Query(value = "SELECT f FROM FalseAlarmMachineSummary f WHERE f.machineStationCode = :machineStationCode",
            countQuery = "SELECT count(f) FROM FalseAlarmMachineSummary f WHERE f.machineStationCode = :machineStationCode")
    List<FalseAlarmMachineSummary> findByMachineStationCode(@Param("machineStationCode") int machineStationCode);

    /*
    * Trigger stored procedure
    * */
    @Procedure(procedureName = "ProcessFalseAlarms")
    void triggerStoredProcedure();

    /**
     * Retrieves a list of false alarm summaries(contains the failed part data) for a specific machine, rack, and channel.
     * This method is transactional to ensure consistency during the data retrieval process.
     *
     * @param machineCode   the unique code identifying the machine
     * @param rackCode      the code identifying the rack which is compatible with the machine
     * @param channelNumber the number identifying the specific channel of the rack to query
     * @return a list of {@link FalseAlarmMachineSummary} objects representing the false alarms
     */
    @Query(value = "SELECT f FROM FalseAlarmMachineSummary f WHERE f.machineStationCode = :machineCode AND f.rackCode = :rackCode AND f.channelNumber = :channelNumber")
    List<FalseAlarmMachineSummary> findByMachineParameters(int machineCode,int rackCode,int channelNumber);
}
