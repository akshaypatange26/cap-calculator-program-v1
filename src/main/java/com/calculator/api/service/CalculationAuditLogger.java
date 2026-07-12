package com.calculator.api.service;

import com.calculator.api.entity.CalculationHistory;
import com.calculator.api.entity.CalculationErrorLog;
import com.calculator.api.repository.CalculationHistoryRepository;
import com.calculator.api.repository.CalculationErrorLogRepository;
import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CalculationAuditLogger {

    private final CalculationHistoryRepository historyRepository;
    private final CalculationErrorLogRepository errorLogRepository;

    @Async
    public void logSuccess(CalculationRequest request, CalculationResponse response,
                           String messageId, String correlationId, String consumerType, String clientId) {
        try {
            CalculationHistory history = new CalculationHistory();
            history.setMessageId(messageId);
            history.setCorrelationId(correlationId);
            history.setConsumerType(consumerType);
            history.setClientId(clientId);

            history.setOperand1(request.getOperands().getOperand1());
            history.setOperand2(request.getOperands().getOperand2());
            history.setOperation(request.getOperation().name());
            history.setResultValue(response.getResult().getData().getValue());

            historyRepository.save(history);
        } catch (Exception e) {
            log.error("Failed to save calculation history asynchronously", e);
        }
    }

    @Async
    public void logError(String errorCode, String errorMessage, String errorDetails,
                         String messageId, String correlationId, String consumerType, String clientId) {
        try {
            CalculationErrorLog errorLog = new CalculationErrorLog();
            errorLog.setMessageId(messageId);
            errorLog.setCorrelationId(correlationId);
            errorLog.setConsumerType(consumerType);
            errorLog.setClientId(clientId);

            errorLog.setErrorCode(errorCode);
            errorLog.setErrorMessage(errorMessage);
            errorLog.setErrorDetails(errorDetails);

            errorLogRepository.save(errorLog);
        } catch (Exception e) {
            log.error("Failed to save calculation error log asynchronously", e);
        }
    }
}
