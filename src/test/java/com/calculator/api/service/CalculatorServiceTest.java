package com.calculator.api.service;


import com.calculator.api.processor.CalculatorProcessor;
import com.calculator.api.utility.RequestValidator;
import com.calculator.model.CalculationRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;


class CalculatorServiceTest {


    private final CalculatorProcessor processor =
            Mockito.mock(CalculatorProcessor.class);


    private final RequestValidator validator =
            Mockito.mock(RequestValidator.class);

    private final com.calculator.api.repository.CalculationHistoryRepository historyRepository =
            Mockito.mock(com.calculator.api.repository.CalculationHistoryRepository.class);

    private final CalculatorService service =
            new CalculatorService(
                    processor,
                    validator,
                    historyRepository
            );


    @Test
    void shouldValidateRequestBeforeProcessing() {


        CalculationRequest request =
                new CalculationRequest();


        service.performCalculation(request);


        verify(validator)
                .validate(request);


        verify(processor)
                .process(request);
    }

}