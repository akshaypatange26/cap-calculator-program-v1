package com.calculator.api.service;

import com.calculator.api.entity.CalculationHistory;
import com.calculator.api.processor.CalculatorProcessor;
import com.calculator.api.repository.CalculationHistoryRepository;
import com.calculator.api.utility.Constants;
import com.calculator.api.utility.RequestValidator;
import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculatorService {

    private final CalculatorProcessor calculatorProcessor;
    private final RequestValidator requestValidator;
    private final CalculationHistoryRepository calculationHistoryRepository;
    
    public CalculationResponse performCalculation(CalculationRequest request) {
        requestValidator.validate(request);
        CalculationResponse response = calculatorProcessor.process(request);
        saveCalculationHistory(request, response);
        return response;
    }

    private void saveCalculationHistory(CalculationRequest request, CalculationResponse response) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest httpRequest = attributes.getRequest();
                
                CalculationHistory history = new CalculationHistory();
                history.setMessageId(httpRequest.getHeader(Constants.MESSAGE_ID_HEADER));
                history.setCorrelationId(httpRequest.getHeader(Constants.CORRELATION_ID_HEADER));
                history.setConsumerType(httpRequest.getHeader(Constants.CONSUMER_TYPE_HEADER));
                history.setClientId(httpRequest.getHeader(Constants.CLIENT_ID_HEADER));
                
                history.setOperand1(request.getOperands().getOperand1());
                history.setOperand2(request.getOperands().getOperand2());
                history.setOperation(request.getOperation().name());
                history.setResultValue(response.getResult().getData().getValue());
                
                calculationHistoryRepository.save(history);
            }
        } catch (Exception e) {
            log.error("Failed to save calculation history to database", e);
        }
    }
}