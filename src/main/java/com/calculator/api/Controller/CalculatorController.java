package com.calculator.api;

import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationResponse;
import com.calculator.service.CalculatorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<CalculationResponse> performCalculation(@Valid @RequestBody CalculationRequest calculationRequest) {

        CalculationResponse response = calculatorService.performCalculation(calculationRequest);

        return ResponseEntity.ok(response);
    }
}