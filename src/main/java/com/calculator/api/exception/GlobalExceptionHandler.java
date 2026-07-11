package com.calculator.api.exception;

import com.calculator.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.calculator.utility.Constants;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * Handles invalid JSON payloads and enum conversion errors.
     * Example:
     * {
     *    "operation": "power"
     * }
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {

        String errorMessage = ex.getMostSpecificCause().getMessage();

        ErrorResponse errorResponse = new ErrorResponse();

        if (errorMessage != null && errorMessage.contains(Constants.OPERATION_ENUM_MESSAGE)) {
            errorResponse.setError(
                    Constants.SUPPORTED_OPERATIONS_MESSAGE
            );
            errorResponse.setCode(
                    Constants.VALIDATION_ERROR
            );
        } else {
            errorResponse.setError(Constants.INVALID_JSON_MESSAGE);
            errorResponse.setCode(Constants.INVALID_JSON);
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }


    /**
     * Handles OpenAPI generated Bean Validation failures.
     * Example:
     * Missing operand1, operand2, or operation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + " " + error.getDefaultMessage())
                .findFirst()
                .orElse(Constants.VALIDATION_FAILED_MESSAGE);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(errorMessage);
        errorResponse.setCode(Constants.VALIDATION_ERROR);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }


    /**
     * Handles business validation errors.
     * Example:
     * Divide operation with operand2 = 0
     */
    @ExceptionHandler(DivisionByZeroException.class)
    public ResponseEntity<ErrorResponse> handleDivisionByZero(
            DivisionByZeroException ex) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(ex.getMessage());
        errorResponse.setCode(Constants.DIVISION_BY_ZERO);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }


    /**
     * Handles other illegal requests.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(ex.getMessage());
        errorResponse.setCode(Constants.INVALID_REQUEST);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }


    /**
     * Catch-all exception handler.
     * Never expose internal exception details to API consumers.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex) {

        log.error(Constants.UNEXPECTED_ERROR_MESSAGE, ex);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(Constants.INTERNAL_SERVER_ERROR_MESSAGE);
        errorResponse.setCode(Constants.INTERNAL_ERROR);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperation(
            InvalidOperationException ex) {

        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setError(ex.getMessage());
        errorResponse.setCode(
                Constants.VALIDATION_ERROR
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}