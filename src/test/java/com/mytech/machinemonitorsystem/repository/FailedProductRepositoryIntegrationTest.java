package com.mytech.machinemonitorsystem.repository;

import com.mytech.machinemonitorsystem.entity.FailedProductCumulative;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test") // Use the test database profile
public class FailedProductRepositoryIntegrationTest {

    @Autowired
    private FailedProductRepository failedProductRepository;
    private FailedProductCumulative insertedProduct;

    @BeforeEach
    public void setUp() {
        insertedProduct = new FailedProductCumulative();
        insertedProduct.setProductCode(11664L);
        insertedProduct.setProductSequence(1L);
        insertedProduct.setCumulativeFailCount(5L);
        insertedProduct.setStationCode(1L);
        insertedProduct.setStationChannelNumber(1L);
        insertedProduct.setCreatedAt(LocalDateTime.now());
        failedProductRepository.saveAndFlush(insertedProduct);
    }

//    @AfterEach    --we do not need to clean up as @DataJpaTest will rollback the transaction after each test
//    public void tearDown() {
//        if (insertedProduct != null) {
//            failedProductRepository.deleteById(insertedProduct.getId());
//        }
//    }

    @Test
    public void shouldFindEntityByProductCode_whenExists() {
        List<FailedProductCumulative> results = failedProductRepository.findByProductCode(11664L);
        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());
    }

    @Test
    public void shouldFindEntityByCreatedAtAfter_whenExists() {
        LocalDateTime date = LocalDateTime.of(2025, 1, 1, 0, 0);
        List<FailedProductCumulative> results = failedProductRepository.findByCreatedAtAfter(date);
        Assertions.assertNotNull(results);
        Assertions.assertFalse(results.isEmpty());
    }

    @Test
    public void shouldFindEntityByStation_whenExists() {
        List<FailedProductCumulative> results = failedProductRepository.findFailuresByStationCode(1L);
        Assertions.assertNotNull(results);
        Assertions.assertFalse(results.isEmpty());
    }

    @Test
    public void shouldFindEntityByProductCodeAndStationCodeAndStationChannelNumber_whenExists() {
        List<FailedProductCumulative> results = failedProductRepository.findByProductCodeAndStationCodeAndStationChannelNumber(11664L, 1L, 1L);
        Assertions.assertNotNull(results);
        Assertions.assertFalse(results.isEmpty());
    }

    @Test
    public void testFindByStationCodeAndStationChannelNumber() {
        List<FailedProductCumulative> results = failedProductRepository.findByStationCodeAndStationChannelNumber(1L, 1L);
        Assertions.assertNotNull(results);
        Assertions.assertFalse(results.isEmpty());
    }
}