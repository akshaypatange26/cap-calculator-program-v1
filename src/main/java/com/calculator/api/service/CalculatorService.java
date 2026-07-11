package com.calculator.service;

import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationResponse;
import com.calculator.processor.CalculatorProcessor;
import com.calculator.utility.Validator;
import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    private final CalculatorProcessor calculatorProcessor;

    public CalculatorService(CalculatorProcessor calculatorProcessor) {
        this.calculatorProcessor = calculatorProcessor;
    }

    public CalculationResponse performCalculation(
            CalculationRequest calculationRequest) {
        Validator.validate(calculationRequest);
        double result = calculatorProcessor.calculate(calculationRequest);
        CalculationResponse response = new CalculationResponse();
        response.setOperand1(calculationRequest.getOperand1());
        response.setOperand2(calculationRequest.getOperand2());
        response.setOperation(
                CalculationResponse.OperationEnum
                        .fromValue(
                                calculationRequest
                                        .getOperation()
                                        .getValue()
                        )
        );

        response.setResult(result);
        return response;
    }
}