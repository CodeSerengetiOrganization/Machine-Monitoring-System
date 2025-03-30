package com.mytech.machinemonitorsystem.repository;

import com.mytech.machinemonitorsystem.entity.FalseAlarmMachineSummary;
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
public class FalseAlarmRepositoryTest {

    @Autowired
    FalseAlarmRepository falseAlarmRepository;

    @Test
    public void testDatabaseConnection(){
        List<FalseAlarmMachineSummary> allFalseAlarms = falseAlarmRepository.findAll();
        Assertions.assertNotNull(allFalseAlarms);
    }
}
