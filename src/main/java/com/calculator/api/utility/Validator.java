package com.calculator.utility;

import com.calculator.api.exception.InvalidOperationException;
import com.calculator.model.CalculationRequest;

public final class Validator {

    private Validator() {
        // Utility class
    }

    public static void validate(CalculationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(Constants.NULL_CALCULATION_REQUEST_MESSAGE);
        }

        if (request.getOperand1() == null) {
            throw new IllegalArgumentException(Constants.VALIDATION_FAILED_MESSAGE);
        }

        if (request.getOperand2() == null) {
            throw new IllegalArgumentException(Constants.OPERAND2_REQUIRED_MESSAGE);
        }


        if (request.getOperation() == null) {
            throw new InvalidOperationException(Constants.SUPPORTED_OPERATIONS_MESSAGE);
        }
        validateOperation(request.getOperation());
    }


    private static void validateOperation(CalculationRequest.OperationEnum operation) {

        boolean validOperation = operation == CalculationRequest.OperationEnum.ADD || operation == CalculationRequest.OperationEnum.SUBTRACT
                        || operation == CalculationRequest.OperationEnum.MULTIPLY || operation == CalculationRequest.OperationEnum.DIVIDE;
        if (!validOperation) {
            throw new InvalidOperationException(Constants.SUPPORTED_OPERATIONS_MESSAGE);
        }
    }
}