package com.calculator.api.controller;

import com.calculator.api.CalculatorApi;
import com.calculator.api.service.CalculatorService;
import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CalculatorController implements CalculatorApi {

    private final CalculatorService calculatorService;

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
}