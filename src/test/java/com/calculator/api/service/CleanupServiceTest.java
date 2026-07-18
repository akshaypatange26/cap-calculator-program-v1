package com.calculator.api.service;

import com.calculator.api.repository.CalculationErrorLogRepository;
import com.calculator.api.repository.CalculationHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CleanupServiceTest {

    private CalculationHistoryRepository historyRepository;
    private CalculationErrorLogRepository errorRepository;
    private CleanupService cleanupService;

    @BeforeEach
    void setUp() {
        historyRepository = mock(CalculationHistoryRepository.class);
        errorRepository = mock(CalculationErrorLogRepository.class);
        cleanupService = new CleanupService(historyRepository, errorRepository);
    }

    @Test
    void shouldPruneOldLogs() {
        when(historyRepository.deleteByCreatedAtBefore(any(LocalDateTime.class))).thenReturn(5);
        when(errorRepository.deleteByCreatedAtBefore(any(LocalDateTime.class))).thenReturn(10);

        cleanupService.pruneOldLogs();

        verify(historyRepository).deleteByCreatedAtBefore(any(LocalDateTime.class));
        verify(errorRepository).deleteByCreatedAtBefore(any(LocalDateTime.class));
    }

    @Test
    void shouldHandlePruneErrorsGracefully() {
        doThrow(new RuntimeException("Database down")).when(historyRepository).deleteByCreatedAtBefore(any(LocalDateTime.class));

        cleanupService.pruneOldLogs();

        verify(historyRepository).deleteByCreatedAtBefore(any(LocalDateTime.class));
        verifyNoInteractions(errorRepository);
    }
}
