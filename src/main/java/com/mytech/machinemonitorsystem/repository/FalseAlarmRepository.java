package com.mytech.machinemonitorsystem.repository;

import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FalseAlarmRepository extends JpaRepository<FalseAlarmMachineSummary,Integer > {
}
