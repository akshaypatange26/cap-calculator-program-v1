package com.calculator.api;

import com.calculator.model.CalculationRequest;
import com.calculator.model.CalculationResponse;
import com.calculator.model.ErrorResponse;
import com.calculator.model.HealthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    @PostMapping("/calculate")
    public ResponseEntity<?> performCalculation(@Valid @RequestBody CalculationRequest calculationRequest) {
        try {
            // Validate operation
            if (calculationRequest.getOperation() == null) {
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setError("Operation is required. Valid values: add, subtract, multiply, divide");
                errorResponse.setCode("MISSING_OPERATION");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Validate operands
            if (calculationRequest.getOperand1() == null) {
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setError("operand1 is required and must be a valid number");
                errorResponse.setCode("MISSING_OPERAND1");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (calculationRequest.getOperand2() == null) {
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setError("operand2 is required and must be a valid number");
                errorResponse.setCode("MISSING_OPERAND2");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            double result;
            CalculationRequest.OperationEnum operation = calculationRequest.getOperation();

            switch (operation) {
                case ADD:
                    result = calculationRequest.getOperand1() + calculationRequest.getOperand2();
                    break;
                case SUBTRACT:
                    result = calculationRequest.getOperand1() - calculationRequest.getOperand2();
                    break;
                case MULTIPLY:
                    result = calculationRequest.getOperand1() * calculationRequest.getOperand2();
                    break;
                case DIVIDE:
                    if (calculationRequest.getOperand2() == 0) {
                        ErrorResponse errorResponse = new ErrorResponse();
                        errorResponse.setError("Cannot divide by zero. operand2 must not be 0");
                        errorResponse.setCode("DIVISION_BY_ZERO");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
                    }
                    result = calculationRequest.getOperand1() / calculationRequest.getOperand2();
                    break;
                default:
                    ErrorResponse errorResponse = new ErrorResponse();
                    errorResponse.setError("Invalid operation. Supported operations are: add, subtract, multiply, divide");
                    errorResponse.setCode("INVALID_OPERATION");
                    return ResponseEntity.badRequest().body(errorResponse);
            }

            CalculationResponse response = new CalculationResponse();
            response.setOperand1(calculationRequest.getOperand1());
            response.setOperand2(calculationRequest.getOperand2());
            response.setOperation(CalculationResponse.OperationEnum.fromValue(calculationRequest.getOperation().getValue()));
            response.setResult(result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError("Internal server error: " + e.getMessage());
            errorResponse.setCode("INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> healthCheck() {
        HealthResponse response = new HealthResponse();
        response.setStatus(HealthResponse.StatusEnum.UP);
        response.setMessage("Calculator service is running");
        return ResponseEntity.ok(response);
    }
}
