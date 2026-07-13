package com.calculator.api.controller;

import com.calculator.api.service.CalculatorService;
import com.calculator.api.service.HistoryService;
import com.calculator.model.CalculationErrorRecord;
import com.calculator.model.CalculationHistoryRecord;
import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CalculatorControllerTest {

    private CalculatorService calculatorService;
    private HistoryService historyService;
    private CalculatorController controller;

    @BeforeEach
    void setUp() {
        calculatorService = mock(CalculatorService.class);
        historyService = mock(HistoryService.class);
        controller = new CalculatorController(calculatorService, historyService);
    }

    @Test
    void shouldReturnCalculationResponse() {
        CalculationRequest request = new CalculationRequest();
        CalculationResponse response = new CalculationResponse();

        when(calculatorService.performCalculation(request)).thenReturn(response);

        assertEquals(
                response,
                controller.performCalculation(
                        "MSG-123",
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                        "WEB_APP",
                        "calculator-ui",
                        request
                ).getBody());

        verify(calculatorService).performCalculation(request);
    }

    @Test
    void shouldReturnCalculationHistory() {
        CalculationHistoryRecord record = new CalculationHistoryRecord();
        List<CalculationHistoryRecord> expectedHistory = Collections.singletonList(record);

        when(historyService.getHistory()).thenReturn(expectedHistory);

        ResponseEntity<List<CalculationHistoryRecord>> response = controller.getCalculationHistory();

        assertEquals(expectedHistory, response.getBody());
        verify(historyService).getHistory();
    }

    @Test
    void shouldReturnCalculationErrors() {
        CalculationErrorRecord record = new CalculationErrorRecord();
        List<CalculationErrorRecord> expectedErrors = Collections.singletonList(record);

        when(historyService.getErrors()).thenReturn(expectedErrors);

        ResponseEntity<List<CalculationErrorRecord>> response = controller.getCalculationErrors();

        assertEquals(expectedErrors, response.getBody());
        verify(historyService).getErrors();
    }
}