package com.calculator.api.controller;

import com.calculator.api.CalculatorApi;
import com.calculator.api.service.CalculatorService;
import com.calculator.api.service.HistoryService;
import com.calculator.model.CalculationErrorRecord;
import com.calculator.model.CalculationHistoryRecord;
import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CalculatorController implements CalculatorApi {

    private final CalculatorService calculatorService;
    private final HistoryService historyService;

    @Override
    public ResponseEntity<CalculationResponse> performCalculation(
            String xMessageId,
            UUID xAppCorrelationId,
            String xConsumerType,
            String xClientId,
            CalculationRequest calculationRequest) {

        return ResponseEntity.ok(
                calculatorService.performCalculation(calculationRequest)
        );
    }

    @Override
    public ResponseEntity<List<CalculationHistoryRecord>> getCalculationHistory() {
        return ResponseEntity.ok(historyService.getHistory());
    }

    @Override
    public ResponseEntity<List<CalculationErrorRecord>> getCalculationErrors() {
        return ResponseEntity.ok(historyService.getErrors());
    }
}