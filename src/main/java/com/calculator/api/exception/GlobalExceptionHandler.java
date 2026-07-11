package com.calculator.api.exception;

import com.calculator.api.utility.Constants;
import com.calculator.model.Error;
import com.calculator.model.ErrorResponse;
import com.calculator.model.ErrorResponseResult;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Invalid JSON / Enum conversion errors
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();

        if (cause instanceof InvalidFormatException invalidFormatException && invalidFormatException.getTargetType().isEnum()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(Constants.INVALID_OPERATION,
                    Constants.SUPPORTED_OPERATIONS_MESSAGE, Constants.SUPPORTED_OPERATIONS_MESSAGE));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(Constants.INVALID_BODY,
                Constants.INVALID_BODY_MESSAGE, Constants.INVALID_JSON_DETAILS));
    }

    /**
     * Bean validation errors
     * Example:
     * operand1 = null
     * operand2 = null
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(Constants.INVALID_BODY,
                Constants.INVALID_BODY_MESSAGE, ex.getMessage()));
    }

    /**
     * Division by zero
     */
    @ExceptionHandler(com.calculator.api.exception.DivisionByZeroException.class)
    public ResponseEntity<ErrorResponse> handleDivisionByZero(com.calculator.api.exception.DivisionByZeroException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(Constants.INVALID_BODY,
                Constants.INVALID_BODY_MESSAGE, ex.getMessage()));
    }

    /**
     * Invalid operation from processor/service
     */
    @ExceptionHandler(com.calculator.api.exception.InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperation(com.calculator.api.exception.InvalidOperationException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(Constants.INVALID_OPERATION,
                Constants.INVALID_OPERATION_MESSAGE, ex.getMessage()));
    }

    /**
     * Catch unexpected errors
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse(Constants.PROCESSING_ERROR_CODE,
                Constants.PROCESSING_ERROR_MESSAGE, ex.getMessage()));
    }

    private ErrorResponse buildErrorResponse(String code, String message, String details) {
        Error error = new Error();
        error.setCode(code);
        error.setMessage(message);
        error.setDetails(details);
        ErrorResponseResult result = new ErrorResponseResult();
        result.setErrors(List.of(error));
        ErrorResponse response = new ErrorResponse();
        response.setResult(result);
        return response;
    }
}