package com.calculator.api.service;

import com.calculator.api.entity.CalculationErrorLog;
import com.calculator.api.entity.CalculationHistory;
import com.calculator.api.repository.CalculationErrorLogRepository;
import com.calculator.api.repository.CalculationHistoryRepository;
import com.calculator.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CalculationAuditLoggerTest {

    private CalculationHistoryRepository historyRepository;
    private CalculationErrorLogRepository errorLogRepository;
    private CalculationAuditLogger auditLogger;

    @BeforeEach
    void setUp() {
        historyRepository = mock(CalculationHistoryRepository.class);
        errorLogRepository = mock(CalculationErrorLogRepository.class);
        auditLogger = new CalculationAuditLogger(historyRepository, errorLogRepository);
    }

    @Test
    void shouldLogSuccess() {
        CalculationRequest request = new CalculationRequest();
        CalculationRequestOperands operands = new CalculationRequestOperands();
        operands.setOperand1(10.0);
        operands.setOperand2(20.0);
        request.setOperands(operands);
        request.setOperation(CalculationRequest.OperationEnum.ADD);

        CalculationResponse response = new CalculationResponse();
        CalculationResponseResult result = new CalculationResponseResult();
        CalculationResponseResultData data = new CalculationResponseResultData();
        data.setValue(30.0);
        result.setData(data);
        response.setResult(result);

        auditLogger.logSuccess(request, response, "msg-123", "corr-456", "WEB_APP", "client-789");

        ArgumentCaptor<CalculationHistory> historyCaptor = ArgumentCaptor.forClass(CalculationHistory.class);
        verify(historyRepository).save(historyCaptor.capture());

        CalculationHistory saved = historyCaptor.getValue();
        assertEquals("msg-123", saved.getMessageId());
        assertEquals("corr-456", saved.getCorrelationId());
        assertEquals("WEB_APP", saved.getConsumerType());
        assertEquals("client-789", saved.getClientId());
        assertEquals(10.0, saved.getOperand1());
        assertEquals(20.0, saved.getOperand2());
        assertEquals("ADD", saved.getOperation());
        assertEquals(30.0, saved.getResultValue());
    }

    @Test
    void shouldHandleExceptionInLogSuccess() {
        CalculationRequest request = new CalculationRequest();
        CalculationRequestOperands operands = new CalculationRequestOperands();
        operands.setOperand1(10.0);
        operands.setOperand2(20.0);
        request.setOperands(operands);
        request.setOperation(CalculationRequest.OperationEnum.ADD);

        CalculationResponse response = new CalculationResponse();
        CalculationResponseResult result = new CalculationResponseResult();
        CalculationResponseResultData data = new CalculationResponseResultData();
        data.setValue(30.0);
        result.setData(data);
        response.setResult(result);

        when(historyRepository.save(any())).thenThrow(new RuntimeException("DB offline"));

        // Should catch exception and not propagate it
        auditLogger.logSuccess(request, response, "msg-123", "corr-456", "WEB_APP", "client-789");
        verify(historyRepository).save(any());
    }

    @Test
    void shouldLogError() {
        auditLogger.logError("1002", "Invalid JSON", "details here", "msg-123", "corr-456", "WEB_APP", "client-789");

        ArgumentCaptor<CalculationErrorLog> errorCaptor = ArgumentCaptor.forClass(CalculationErrorLog.class);
        verify(errorLogRepository).save(errorCaptor.capture());

        CalculationErrorLog saved = errorCaptor.getValue();
        assertEquals("msg-123", saved.getMessageId());
        assertEquals("corr-456", saved.getCorrelationId());
        assertEquals("WEB_APP", saved.getConsumerType());
        assertEquals("client-789", saved.getClientId());
        assertEquals("1002", saved.getErrorCode());
        assertEquals("Invalid JSON", saved.getErrorMessage());
        assertEquals("details here", saved.getErrorDetails());
    }

    @Test
    void shouldHandleExceptionInLogError() {
        when(errorLogRepository.save(any())).thenThrow(new RuntimeException("DB offline"));

        // Should catch exception and not propagate it
        auditLogger.logError("1002", "Invalid JSON", "details here", "msg-123", "corr-456", "WEB_APP", "client-789");
        verify(errorLogRepository).save(any());
    }
}
