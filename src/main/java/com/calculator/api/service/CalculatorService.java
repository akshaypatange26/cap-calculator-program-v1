package com.calculator.api.service;

import com.calculator.api.processor.CalculatorProcessor;
import com.calculator.api.utility.RequestValidator;
import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final CalculatorProcessor calculatorProcessor;
    private final RequestValidator requestValidator;
    
    public CalculationResponse performCalculation(CalculationRequest request) {

        requestValidator.validate(request);
        return calculatorProcessor.process(request);
    }
}