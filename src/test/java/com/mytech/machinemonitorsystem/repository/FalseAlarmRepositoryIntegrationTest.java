package com.mytech.machinemonitorsystem.repository;

import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("dev") // Activates the "dev" profile
public class FalseAlarmRepositoryIntegrationTest {

    @Autowired
    FalseAlarmRepository falseAlarmRepository;

    @PersistenceContext // Injects the EntityManager
    private EntityManager entityManager;

    @Test
    public void testDatabaseConnection(){
        List<FalseAlarmMachineSummary> allFalseAlarms = falseAlarmRepository.findAll();
        Assertions.assertNotNull(allFalseAlarms);
    }

    @Test
    public void testFindByStationCode(){
        List<FalseAlarmMachineSummary> byStationCode = falseAlarmRepository.findByMachineStationCode(4);
        Assertions.assertNotNull(byStationCode);
        Assertions.assertEquals(12,byStationCode.size());
        System.out.println("byStationCode:"+byStationCode.toString());
    }

    @Test
    @Transactional
    public void triggerStoredProcedureProcessFalseAlarManualCheck(){
        falseAlarmRepository.triggerStoredProcedure();
        entityManager.flush();  //need this to ensure execution
        //retrieve the results in the same
        List<FalseAlarmMachineSummary> falseAlarms = falseAlarmRepository.findByMachineStationCode(4);
        System.out.println("falseAlarms sie:"+falseAlarms.size());
        Assertions.assertTrue(falseAlarms.size() == 12);
    }
}
