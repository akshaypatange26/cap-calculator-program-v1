package com.calculator.api.processor;

import com.calculator.api.exception.DivisionByZeroException;
import com.calculator.api.utility.Constants;
import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationRequestOperands;
import com.calculator.model.CalculationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorProcessorTest {

    private CalculatorProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new CalculatorProcessor();
    }

    @Test
    void shouldAddNumbers() {

        CalculationRequest request = buildRequest(
                10.0,
                5.0,
                CalculationRequest.OperationEnum.ADD);

        CalculationResponse response = processor.process(request);

        assertNotNull(response);
        assertEquals(15.0,
                response.getResult().getData().getValue());

        assertEquals("add",
                response.getResult().getData().getOperation());

        assertEquals(10.0,
                response.getResult().getData()
                        .getOperands().getOperand1());

        assertEquals(5.0,
                response.getResult().getData()
                        .getOperands().getOperand2());
    }

    @Test
    void shouldSubtractNumbers() {

        CalculationRequest request = buildRequest(
                10.0,
                3.0,
                CalculationRequest.OperationEnum.SUBTRACT);

        CalculationResponse response = processor.process(request);

        assertEquals(7.0,
                response.getResult().getData().getValue());

        assertEquals("subtract",
                response.getResult().getData().getOperation());
    }

    @Test
    void shouldMultiplyNumbers() {

        CalculationRequest request = buildRequest(
                6.0,
                4.0,
                CalculationRequest.OperationEnum.MULTIPLY);

        CalculationResponse response = processor.process(request);

        assertEquals(24.0,
                response.getResult().getData().getValue());

        assertEquals("multiply",
                response.getResult().getData().getOperation());
    }

    @Test
    void shouldDivideNumbers() {

        CalculationRequest request = buildRequest(
                20.0,
                5.0,
                CalculationRequest.OperationEnum.DIVIDE);

        CalculationResponse response = processor.process(request);

        assertEquals(4.0,
                response.getResult().getData().getValue());

        assertEquals("divide",
                response.getResult().getData().getOperation());
    }

    @Test
    void shouldThrowDivisionByZeroException() {

        CalculationRequest request = buildRequest(
                10.0,
                0.0,
                CalculationRequest.OperationEnum.DIVIDE);

        DivisionByZeroException exception =
                assertThrows(
                        DivisionByZeroException.class,
                        () -> processor.process(request));

        assertEquals(
                Constants.DIVISION_BY_ZERO_DETAILS,
                exception.getMessage());
    }

    private CalculationRequest buildRequest(
            Double operand1,
            Double operand2,
            CalculationRequest.OperationEnum operation) {

        CalculationRequest request =
                new CalculationRequest();

        CalculationRequestOperands operands =
                new CalculationRequestOperands();

        operands.setOperand1(operand1);
        operands.setOperand2(operand2);

        request.setOperands(operands);
        request.setOperation(operation);

        return request;
    }
}