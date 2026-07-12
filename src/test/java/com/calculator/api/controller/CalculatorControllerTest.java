package com.calculator.api.controller;

import com.calculator.api.service.CalculatorService;
import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CalculatorControllerTest {

    private CalculatorService calculatorService;

    private CalculatorController controller;

    @BeforeEach
    void setUp() {

        calculatorService = mock(CalculatorService.class);

        controller =
                new CalculatorController(calculatorService);
    }

    @Test
    void shouldReturnCalculationResponse() {

        CalculationRequest request =
                new CalculationRequest();

        CalculationResponse response =
                new CalculationResponse();

        when(calculatorService.performCalculation(request))
                .thenReturn(response);

        assertEquals(
                response,
                controller.performCalculation(
                        "MSG-123",
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                        "WEB_APP",
                        "calculator-ui",
                        request
                ).getBody());

        verify(calculatorService)
                .performCalculation(request);
    }
}