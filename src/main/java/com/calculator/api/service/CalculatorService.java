package com.calculator.api.service;

import com.calculator.api.processor.CalculatorProcessor;
import com.calculator.api.utility.Constants;
import com.calculator.api.utility.RequestValidator;
import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final CalculatorProcessor calculatorProcessor;
    private final RequestValidator requestValidator;
    private final CalculationAuditLogger calculationAuditLogger;

    public CalculationResponse performCalculation(CalculationRequest request) {
        requestValidator.validate(request);
        CalculationResponse response = calculatorProcessor.process(request);
        triggerAuditLog(request, response);
        return response;
    }

    private void triggerAuditLog(CalculationRequest request, CalculationResponse response) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest httpRequest = attributes.getRequest();
            String messageId = httpRequest.getHeader(Constants.MESSAGE_ID_HEADER);
            String correlationId = httpRequest.getHeader(Constants.CORRELATION_ID_HEADER);
            String consumerType = httpRequest.getHeader(Constants.CONSUMER_TYPE_HEADER);
            String clientId = httpRequest.getHeader(Constants.CLIENT_ID_HEADER);

            calculationAuditLogger.logSuccess(request, response, messageId, correlationId, consumerType, clientId);
        }
    }
}