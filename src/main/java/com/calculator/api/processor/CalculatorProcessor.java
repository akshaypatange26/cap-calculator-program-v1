package com.calculator.api.processor;

import com.calculator.api.exception.DivisionByZeroException;
import com.calculator.api.utility.Constants;
import com.calculator.model.*;
import org.springframework.stereotype.Component;

@Component
public class CalculatorProcessor {


    public CalculationResponse process(CalculationRequest request) {

        double operand1 = request.getOperands().getOperand1();
        double operand2 = request.getOperands().getOperand2();
        double result = switch (request.getOperation()) {
            case ADD -> operand1 + operand2;
            case SUBTRACT -> operand1 - operand2;
            case MULTIPLY -> operand1 * operand2;
            case DIVIDE -> {
                if (operand2 == 0) {
                    throw new DivisionByZeroException(Constants.DIVISION_BY_ZERO_DETAILS);
                }
                yield operand1 / operand2;
            }
        };
        return buildResponse(request, result);
    }


    private CalculationResponse buildResponse(CalculationRequest request, double result) {
        CalculationResponse response = new CalculationResponse();
        CalculationResponseResultDataOperands operands = new CalculationResponseResultDataOperands();
        operands.setOperand1(request.getOperands().getOperand1());
        operands.setOperand2(request.getOperands().getOperand2());
        CalculationResponseResultData data = new CalculationResponseResultData();
        data.setOperands(operands);
        data.setOperation(request.getOperation().getValue());
        data.setValue(result);
        CalculationResponseResult responseResult = new CalculationResponseResult();
        responseResult.setData(data);
        response.setResult(responseResult);
        return response;
    }
}