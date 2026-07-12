package com.calculator.api.utility;

import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationRequestOperands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestValidatorTest {

    private RequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RequestValidator();
    }

    @Test
    void shouldThrowWhenRequestIsNull() {

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validate(null));

        assertEquals(Constants.NULL_REQUEST_DETAILS, ex.getMessage());
    }

    @Test
    void shouldThrowWhenOperandsAreNull() {

        CalculationRequest request = new CalculationRequest();

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validate(request));

        assertEquals(Constants.NULL_OPERANDS_DETAILS, ex.getMessage());
    }

    @Test
    void shouldThrowWhenOperand1Missing() {

        CalculationRequest request = new CalculationRequest();

        CalculationRequestOperands operands =
                new CalculationRequestOperands();

        operands.setOperand2(10.0);

        request.setOperands(operands);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validate(request));

        assertEquals(Constants.OPERAND1_REQUIRED_DETAILS, ex.getMessage());
    }

    @Test
    void shouldThrowWhenOperand2Missing() {

        CalculationRequest request = new CalculationRequest();

        CalculationRequestOperands operands =
                new CalculationRequestOperands();

        operands.setOperand1(10.0);

        request.setOperands(operands);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validate(request));

        assertEquals(Constants.OPERAND2_REQUIRED_DETAILS, ex.getMessage());
    }

    @Test
    void shouldThrowWhenOperationMissing() {

        CalculationRequest request = new CalculationRequest();

        CalculationRequestOperands operands =
                new CalculationRequestOperands();

        operands.setOperand1(10.0);
        operands.setOperand2(20.0);

        request.setOperands(operands);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> validator.validate(request));

        assertEquals(Constants.OPERATION_REQUIRED_DETAILS, ex.getMessage());
    }

    @Test
    void shouldPassValidation() {

        CalculationRequest request = new CalculationRequest();

        CalculationRequestOperands operands =
                new CalculationRequestOperands();

        operands.setOperand1(10.0);
        operands.setOperand2(20.0);

        request.setOperands(operands);
        request.setOperation(CalculationRequest.OperationEnum.ADD);

        assertDoesNotThrow(() -> validator.validate(request));
    }
}