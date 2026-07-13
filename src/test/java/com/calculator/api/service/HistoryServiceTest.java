package com.calculator.api.service;

import com.calculator.api.entity.CalculationErrorLog;
import com.calculator.api.entity.CalculationHistory;
import com.calculator.api.repository.CalculationErrorLogRepository;
import com.calculator.api.repository.CalculationHistoryRepository;
import com.calculator.model.CalculationErrorRecord;
import com.calculator.model.CalculationHistoryRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class HistoryServiceTest {

    private CalculationHistoryRepository historyRepository;
    private CalculationErrorLogRepository errorRepository;
    private HistoryService historyService;

    @BeforeEach
    void setUp() {
        historyRepository = mock(CalculationHistoryRepository.class);
        errorRepository = mock(CalculationErrorLogRepository.class);
        historyService = new HistoryService(historyRepository, errorRepository);
    }

    @Test
    void shouldReturnHistoryRecords() {
        CalculationHistory history = new CalculationHistory();
        history.setId(1L);
        history.setMessageId("MSG-1");
        history.setCorrelationId("CORR-1");
        history.setConsumerType("WEB");
        history.setClientId("CLIENT");
        history.setOperand1(10.0);
        history.setOperand2(5.0);
        history.setOperation("add");
        history.setResultValue(15.0);
        history.setCreatedAt(LocalDateTime.now());

        when(historyRepository.findAll()).thenReturn(Collections.singletonList(history));

        List<CalculationHistoryRecord> result = historyService.getHistory();

        assertEquals(1, result.size());
        CalculationHistoryRecord record = result.get(0);
        assertEquals(history.getId(), record.getId());
        assertEquals(history.getMessageId(), record.getMessageId());
        assertEquals(history.getCorrelationId(), record.getCorrelationId());
        assertEquals(history.getConsumerType(), record.getConsumerType());
        assertEquals(history.getClientId(), record.getClientId());
        assertEquals(history.getOperand1(), record.getOperand1());
        assertEquals(history.getOperand2(), record.getOperand2());
        assertEquals(history.getOperation(), record.getOperation());
        assertEquals(history.getResultValue(), record.getResultValue());
        assertNotNull(record.getCreatedAt());
        verify(historyRepository).findAll();
    }

    @Test
    void shouldReturnErrorRecords() {
        CalculationErrorLog errorLog = new CalculationErrorLog();
        errorLog.setId(1L);
        errorLog.setMessageId("MSG-1");
        errorLog.setCorrelationId("CORR-1");
        errorLog.setConsumerType("WEB");
        errorLog.setClientId("CLIENT");
        errorLog.setErrorCode("1002");
        errorLog.setErrorMessage("Invalid Body");
        errorLog.setErrorDetails("Details");
        errorLog.setCreatedAt(LocalDateTime.now());

        when(errorRepository.findAll()).thenReturn(Collections.singletonList(errorLog));

        List<CalculationErrorRecord> result = historyService.getErrors();

        assertEquals(1, result.size());
        CalculationErrorRecord record = result.get(0);
        assertEquals(errorLog.getId(), record.getId());
        assertEquals(errorLog.getMessageId(), record.getMessageId());
        assertEquals(errorLog.getCorrelationId(), record.getCorrelationId());
        assertEquals(errorLog.getConsumerType(), record.getConsumerType());
        assertEquals(errorLog.getClientId(), record.getClientId());
        assertEquals(errorLog.getErrorCode(), record.getErrorCode());
        assertEquals(errorLog.getErrorMessage(), record.getErrorMessage());
        assertEquals(errorLog.getErrorDetails(), record.getErrorDetails());
        assertNotNull(record.getCreatedAt());
        verify(errorRepository).findAll();
    }
}
