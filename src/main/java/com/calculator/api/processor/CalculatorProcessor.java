package com.calculator.processor;

import com.calculator.api.exception.DivisionByZeroException;
import com.calculator.model.CalculationRequest;
import com.calculator.utility.Constants;
import org.springframework.stereotype.Component;

@Component
public class CalculatorProcessor {


    public double calculate(CalculationRequest calculationRequest) {
        switch (calculationRequest.getOperation()) {
            case ADD:
                return calculationRequest.getOperand1()
                        + calculationRequest.getOperand2();
            case SUBTRACT:
                return calculationRequest.getOperand1()
                        - calculationRequest.getOperand2();
            case MULTIPLY:
                return calculationRequest.getOperand1()
                        * calculationRequest.getOperand2();
            case DIVIDE:

                if (calculationRequest.getOperand2() == 0) {
                    throw new DivisionByZeroException(
                            Constants.DIVISION_BY_ZERO_MESSAGE
                    );
                }
                return calculationRequest.getOperand1()
                        / calculationRequest.getOperand2();
            default:
                throw new IllegalArgumentException(
                        Constants.UNSUPPORTED_OPERATION_MESSAGE
                );
        }
    }
}