package com.calculator.api.utility;

import com.calculator.model.CalculationRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

    public void validate(CalculationRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(Constants.NULL_REQUEST_DETAILS);
        }

        if (request.getOperands() == null) {
            throw new IllegalArgumentException(Constants.NULL_OPERANDS_DETAILS);
        }

        if (request.getOperands().getOperand1() == null) {
            throw new IllegalArgumentException(Constants.OPERAND1_REQUIRED_DETAILS);
        }

        if (request.getOperands().getOperand2() == null) {
            throw new IllegalArgumentException(Constants.OPERAND2_REQUIRED_DETAILS);
        }

        if (request.getOperation() == null) {
            throw new IllegalArgumentException(Constants.OPERATION_REQUIRED_DETAILS);
        }
    }
}