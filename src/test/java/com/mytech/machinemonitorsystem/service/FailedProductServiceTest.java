package com.mytech.machinemonitorsystem.service;

import com.mytech.machinemonitorsystem.entity.FailedProductCumulative;
import com.mytech.machinemonitorsystem.repository.FailedProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class FailedProductServiceTest {

    @Mock
    private FailedProductRepository failedProductRepository;

    @InjectMocks
    private FailedProductService failedProductService;

    private FailedProductCumulative sample;

    @BeforeEach
    void setUp() {
        sample = new FailedProductCumulative();
        sample.setProductCode(11664L);
        sample.setProductSequence(2L);
        sample.setCumulativeFailCount(5L);
        sample.setStationCode(2L);
        sample.setStationChannelNumber(2L);
        sample.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void findByProductCode_shouldReturnList() {
        when(failedProductRepository.findByProductCode(11664L))
                .thenReturn(List.of(sample));

        List<FailedProductCumulative> results = failedProductService.findByProductCode(11664L);

        assertThat(results)
                .isNotEmpty()
                .containsExactly(sample);
    }

    @Test
    void findByCreatedAtAfter_shouldReturnList() {
        LocalDateTime date = LocalDateTime.now().minusDays(1);
        when(failedProductRepository.findByCreatedAtAfter(date))
                .thenReturn(List.of(sample));

        List<FailedProductCumulative> results = failedProductService.findByCreatedAtAfter(date);

        assertThat(results)
                .isNotEmpty()
                .containsExactly(sample);
    }

    @Test
    void findFailuresByStationCode_shouldReturnList() {
        when(failedProductRepository.findFailuresByStationCode(1L))
                .thenReturn(List.of(sample));

        List<FailedProductCumulative> results = failedProductService.findFailuresByStationCode(1L);

        assertThat(results)
                .isNotEmpty()
                .containsExactly(sample);
    }

    @Test
    void findByProductCodeAndStationCodeAndStationChannelNumber_shouldReturnList() {
        when(failedProductRepository.findByProductCodeAndStationCodeAndStationChannelNumber(11664L, 1L, 1L))
                .thenReturn(List.of(sample));

        List<FailedProductCumulative> results = failedProductService
                .findByProductCodeAndStationCodeAndStationChannelNumber(11664L, 1L, 1L);

        assertThat(results)
                .isNotEmpty()
                .containsExactly(sample);
    }

    @Test
    void findByStationCodeAndStationChannelNumber_shouldReturnList() {
        when(failedProductRepository.findByStationCodeAndStationChannelNumber(1L, 1L))
                .thenReturn(List.of(sample));

        List<FailedProductCumulative> results = failedProductService.findByStationCodeAndStationChannelNumber(1L, 1L);

        assertThat(results)
                .isNotEmpty()
                .containsExactly(sample);
    }

    @Test
    void findByProductCode_shouldReturnEmptyListWhenNoData() {
        when(failedProductRepository.findByProductCode(999L))
                .thenReturn(Collections.emptyList());

        List<FailedProductCumulative> results = failedProductService.findByProductCode(999L);

        assertThat(results).isEmpty();
    }
}
