package com.calculator.api.exception;

import com.calculator.api.service.CalculationAuditLogger;
import com.calculator.api.utility.Constants;
import com.calculator.model.Error;
import com.calculator.model.ErrorResponse;
import com.calculator.model.ErrorResponseResult;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final CalculationAuditLogger calculationAuditLogger;

    private void saveErrorLog(String errorCode, String errorMessage, String errorDetails) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest httpRequest = attributes.getRequest();
                String messageId = httpRequest.getHeader(Constants.MESSAGE_ID_HEADER);
                String correlationId = httpRequest.getHeader(Constants.CORRELATION_ID_HEADER);
                String consumerType = httpRequest.getHeader(Constants.CONSUMER_TYPE_HEADER);
                String clientId = httpRequest.getHeader(Constants.CLIENT_ID_HEADER);
                
                calculationAuditLogger.logError(errorCode, errorMessage, errorDetails, messageId, correlationId, consumerType, clientId);
            }
        } catch (Exception e) {
            log.error("Failed to trigger calculation error logging", e);
        }
    }

    /*
     * Invalid JSON + Invalid Enum
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        if (isInvalidOperation(ex)) {
            log.warn("Invalid operation requested: {}", ex.getMessage());
            saveErrorLog(Constants.INVALID_OPERATION_CODE, Constants.INVALID_OPERATION_MESSAGE, Constants.SUPPORTED_OPERATIONS_MESSAGE);
            return ResponseEntity.badRequest().body(buildErrorResponse(List.of(createError(Constants.INVALID_OPERATION_CODE, Constants.INVALID_OPERATION_MESSAGE, Constants.SUPPORTED_OPERATIONS_MESSAGE))));
        }
        log.warn("Invalid JSON or body format: {}", ex.getMessage());
        saveErrorLog(Constants.INVALID_BODY_CODE, Constants.INVALID_BODY_MESSAGE, Constants.INVALID_JSON_DETAILS);
        return ResponseEntity.badRequest().body(buildErrorResponse(List.of(createError(Constants.INVALID_BODY_CODE, Constants.INVALID_BODY_MESSAGE, Constants.INVALID_JSON_DETAILS))));
    }

    private boolean isInvalidOperation(HttpMessageNotReadableException ex) {
        Throwable cause = ex;
        while (cause != null) {
            // OpenAPI generated enum failure
            if (cause instanceof com.fasterxml.jackson.databind.exc.ValueInstantiationException) {
                return true;
            }
            // Normal Jackson enum failure
            if (cause instanceof InvalidFormatException invalidFormatException) {
                return invalidFormatException.getTargetType().isEnum();
            }
            // Generated enum fromValue() throws IllegalArgumentException
            if (cause instanceof IllegalArgumentException && cause.getMessage() != null && cause.getMessage().contains(Constants.UNEXPECTED_VALUE_DETAILS)) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    /*
     * Bean validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Payload validation failed for field: {}", ex.getBindingResult().getFieldErrors());
        List<Error> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String details = switch (fieldError.getField()) {
                case "operation" -> Constants.OPERATION_REQUIRED_DETAILS;
                case "operands" -> Constants.NULL_OPERANDS_DETAILS;
                case "operands.operand1" -> Constants.OPERAND1_REQUIRED_DETAILS;
                case "operands.operand2" -> Constants.OPERAND2_REQUIRED_DETAILS;
                default -> fieldError.getDefaultMessage();
            };
            saveErrorLog(Constants.INVALID_BODY_CODE, Constants.INVALID_BODY_MESSAGE, details);
            errors.add(createError(Constants.INVALID_BODY_CODE, Constants.INVALID_BODY_MESSAGE, details));
        });
        return ResponseEntity.badRequest().body(buildErrorResponse(errors));
    }

    /*
     * Custom validator errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument in request processing: {}", ex.getMessage());
        saveErrorLog(Constants.INVALID_BODY_CODE, Constants.INVALID_BODY_MESSAGE, ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(List.of(createError(Constants.INVALID_BODY_CODE, Constants.INVALID_BODY_MESSAGE, ex.getMessage()))));
    }

    @ExceptionHandler(DivisionByZeroException.class)
    public ResponseEntity<ErrorResponse> handleDivisionByZero(DivisionByZeroException ex) {
        log.warn("Division by zero requested: {}", ex.getMessage());
        saveErrorLog(Constants.INVALID_BODY_CODE, Constants.INVALID_BODY_MESSAGE, ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(List.of(createError(Constants.INVALID_BODY_CODE, Constants.INVALID_BODY_MESSAGE, ex.getMessage()))));
    }

    @ExceptionHandler(InvalidHeaderException.class)
    public ResponseEntity<ErrorResponse> handleInvalidHeader(InvalidHeaderException ex) {
        log.warn("Invalid HTTP headers detected: {}", ex.getErrors());
        List<Error> errors = ex.getErrors().stream().map(error -> {
            saveErrorLog(Constants.INVALID_HEADER_CODE, Constants.INVALID_HEADER_MESSAGE, error);
            return createError(Constants.INVALID_HEADER_CODE, Constants.INVALID_HEADER_MESSAGE, error);
        }).toList();
        return ResponseEntity.badRequest().body(buildErrorResponse(errors));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException ex, HttpServletRequest request) {
        log.warn("Missing required request header: {}", ex.getMessage());
        List<Error> errors = new ArrayList<>();
        validateHeader(request, Constants.MESSAGE_ID_HEADER, errors);
        validateHeader(request, Constants.CORRELATION_ID_HEADER, errors);
        validateHeader(request, Constants.CONSUMER_TYPE_HEADER, errors);
        validateHeader(request, Constants.CLIENT_ID_HEADER, errors);
        return ResponseEntity.badRequest().body(buildErrorResponse(errors));
    }

    private void validateHeader(HttpServletRequest request, String header, List<Error> errors) {
        if (request.getHeader(header) == null) {
            String details = Constants.MISSING_HEADER_DETAILS + header;
            saveErrorLog(Constants.INVALID_HEADER_CODE, Constants.INVALID_HEADER_MESSAGE, details);
            errors.add(createError(Constants.INVALID_HEADER_CODE, Constants.INVALID_HEADER_MESSAGE, details));
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Internal processing error occurred: ", ex);
        saveErrorLog(Constants.PROCESSING_ERROR_CODE, Constants.PROCESSING_ERROR_MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse(List.of(createError(Constants.PROCESSING_ERROR_CODE, Constants.PROCESSING_ERROR_MESSAGE, ex.getMessage()))));
    }

    private Error createError(String code, String message, String details) {
        Error error = new Error();
        error.setCode(code);
        error.setMessage(message);
        error.setDetails(details);
        return error;
    }

    private ErrorResponse buildErrorResponse(List<Error> errors) {
        ErrorResponseResult result = new ErrorResponseResult();
        result.setErrors(errors);
        ErrorResponse response = new ErrorResponse();
        response.setResult(result);
        return response;
    }
}