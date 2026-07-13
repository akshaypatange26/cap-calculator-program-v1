package com.calculator.api.service;

import com.calculator.api.entity.CalculationErrorLog;
import com.calculator.api.entity.CalculationHistory;
import com.calculator.api.repository.CalculationErrorLogRepository;
import com.calculator.api.repository.CalculationHistoryRepository;
import com.calculator.model.CalculationErrorRecord;
import com.calculator.model.CalculationHistoryRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final CalculationHistoryRepository historyRepository;
    private final CalculationErrorLogRepository errorRepository;

    public List<CalculationHistoryRecord> getHistory() {
        return historyRepository.findAll().stream()
                .map(this::mapToHistoryRecord)
                .collect(Collectors.toList());
    }

    public List<CalculationErrorRecord> getErrors() {
        return errorRepository.findAll().stream()
                .map(this::mapToErrorRecord)
                .collect(Collectors.toList());
    }

    private CalculationHistoryRecord mapToHistoryRecord(CalculationHistory history) {
        CalculationHistoryRecord record = new CalculationHistoryRecord();
        record.setId(history.getId());
        record.setMessageId(history.getMessageId());
        record.setCorrelationId(history.getCorrelationId());
        record.setConsumerType(history.getConsumerType());
        record.setClientId(history.getClientId());
        record.setOperand1(history.getOperand1());
        record.setOperand2(history.getOperand2());
        record.setOperation(history.getOperation());
        record.setResultValue(history.getResultValue());
        record.setCreatedAt(toOffsetDateTime(history.getCreatedAt()));
        return record;
    }

    private CalculationErrorRecord mapToErrorRecord(CalculationErrorLog errorLog) {
        CalculationErrorRecord record = new CalculationErrorRecord();
        record.setId(errorLog.getId());
        record.setMessageId(errorLog.getMessageId());
        record.setCorrelationId(errorLog.getCorrelationId());
        record.setConsumerType(errorLog.getConsumerType());
        record.setClientId(errorLog.getClientId());
        record.setErrorCode(errorLog.getErrorCode());
        record.setErrorMessage(errorLog.getErrorMessage());
        record.setErrorDetails(errorLog.getErrorDetails());
        record.setCreatedAt(toOffsetDateTime(errorLog.getCreatedAt()));
        return record;
    }

    private OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
}
