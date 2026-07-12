package com.calculator.api.service;

import com.calculator.api.processor.CalculatorProcessor;
import com.calculator.api.utility.RequestValidator;
import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CalculatorServiceTest {

    private final CalculatorProcessor processor = mock(CalculatorProcessor.class);
    private final RequestValidator validator = mock(RequestValidator.class);
    private final CalculationAuditLogger auditLogger = mock(CalculationAuditLogger.class);

    private final CalculatorService service = new CalculatorService(
            processor,
            validator,
            auditLogger
    );

    @Test
    void shouldValidateRequestBeforeProcessing() {
        CalculationRequest request = new CalculationRequest();
        CalculationResponse response = new CalculationResponse();
        when(processor.process(request)).thenReturn(response);

        // Setup RequestContext
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("x-messageId", "MSG-1");
        mockRequest.addHeader("x-appCorrelationId", "550e8400-e29b-41d4-a716-446655440000");
        mockRequest.addHeader("x-consumerType", "WEB_APP");
        mockRequest.addHeader("x-client-id", "calculator-ui");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        try {
            CalculationResponse result = service.performCalculation(request);

            Assertions.assertEquals(response, result);
            verify(validator).validate(request);
            verify(processor).process(request);
            verify(auditLogger).logSuccess(request, response, "MSG-1", "550e8400-e29b-41d4-a716-446655440000", "WEB_APP", "calculator-ui");
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }
}