package com.calculator.api.exception;

import com.calculator.api.service.CalculationAuditLogger;
import com.calculator.api.utility.Constants;
import com.calculator.model.CalculationRequest;
import com.calculator.model.Error;
import com.calculator.model.ErrorResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final CalculationAuditLogger auditLogger = mock(CalculationAuditLogger.class);
    private GlobalExceptionHandler handler;
    private MockHttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler(auditLogger);
        mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("x-messageId", "MSG-1");
        mockRequest.addHeader("x-appCorrelationId", "550e8400-e29b-41d4-a716-446655440000");
        mockRequest.addHeader("x-consumerType", "WEB_APP");
        mockRequest.addHeader("x-client-id", "calculator-ui");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException(Constants.NULL_REQUEST_DETAILS)
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Constants.INVALID_BODY_CODE, response.getBody().getResult().getErrors().get(0).getCode());
        assertEquals(Constants.NULL_REQUEST_DETAILS, response.getBody().getResult().getErrors().get(0).getDetails());
    }

    @Test
    void shouldHandleDivisionByZeroException() {
        ResponseEntity<ErrorResponse> response = handler.handleDivisionByZero(
                new DivisionByZeroException(Constants.DIVISION_BY_ZERO_DETAILS)
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Constants.DIVISION_BY_ZERO_DETAILS, response.getBody().getResult().getErrors().get(0).getDetails());
    }

    @Test
    void shouldHandleInvalidHeaderException() {
        InvalidHeaderException exception = new InvalidHeaderException(
                List.of("Missing x-messageId", "Missing x-client-id")
        );

        ResponseEntity<ErrorResponse> response = handler.handleInvalidHeader(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getResult().getErrors().size());
    }

    @Test
    void shouldHandleMissingHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MissingRequestHeaderException exception = mock(MissingRequestHeaderException.class);

        ResponseEntity<ErrorResponse> response = handler.handleMissingHeader(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(4, response.getBody().getResult().getErrors().size());
    }

    @Test
    void shouldHandleGenericException() {
        ResponseEntity<ErrorResponse> response = handler.handleException(new Exception("Unexpected error"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Constants.PROCESSING_ERROR_CODE, response.getBody().getResult().getErrors().get(0).getCode());
    }

    @Test
    void shouldHandleHttpMessageNotReadable() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(Constants.INVALID_JSON_DETAILS);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Constants.INVALID_BODY_CODE, response.getBody().getResult().getErrors().get(0).getCode());
    }

    @Test
    void shouldHandleValidationErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "operation", "must not be null"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Constants.OPERATION_REQUIRED_DETAILS, response.getBody().getResult().getErrors().get(0).getDetails());
    }

    @Test
    void shouldHandleAllValidationErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "operation", "must not be null"));
        bindingResult.addError(new FieldError("request", "operands", "must not be null"));
        bindingResult.addError(new FieldError("request", "operands.operand1", "must not be null"));
        bindingResult.addError(new FieldError("request", "operands.operand2", "must not be null"));
        bindingResult.addError(new FieldError("request", "otherField", "default message"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertNotNull(response.getBody());
        List<Error> errors = response.getBody().getResult().getErrors();

        assertEquals(5, errors.size());
        assertEquals(Constants.OPERATION_REQUIRED_DETAILS, errors.get(0).getDetails());
        assertEquals(Constants.NULL_OPERANDS_DETAILS, errors.get(1).getDetails());
        assertEquals(Constants.OPERAND1_REQUIRED_DETAILS, errors.get(2).getDetails());
        assertEquals(Constants.OPERAND2_REQUIRED_DETAILS, errors.get(3).getDetails());
        assertEquals("default message", errors.get(4).getDetails());
    }

    @Test
    void shouldHandleInvalidFormatEnum() {
        InvalidFormatException invalidFormat = InvalidFormatException.from(
                null,
                "Invalid enum",
                "wrong",
                CalculationRequest.OperationEnum.class
        );

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error", invalidFormat);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertNotNull(response.getBody());
        Error error = response.getBody().getResult().getErrors().get(0);

        assertEquals(Constants.INVALID_OPERATION_CODE, error.getCode());
    }

    @Test
    void shouldHandleInvalidOperationEnum() {
        IllegalArgumentException cause = new IllegalArgumentException(Constants.UNEXPECTED_VALUE_DETAILS + " divi");
        ValueInstantiationException valueException = ValueInstantiationException.from(null, "Invalid enum", null, cause);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error", valueException);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertNotNull(response.getBody());
        Error error = response.getBody().getResult().getErrors().get(0);

        assertEquals(Constants.INVALID_OPERATION_CODE, error.getCode());
        assertEquals(Constants.INVALID_OPERATION_MESSAGE, error.getMessage());
        assertEquals(Constants.SUPPORTED_OPERATIONS_MESSAGE, error.getDetails());
    }

    @Test
    void shouldHandleInvalidJsonRequest() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON");

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        Error error = response.getBody().getResult().getErrors().get(0);

        assertEquals(Constants.INVALID_BODY_CODE, error.getCode());
        assertEquals(Constants.INVALID_BODY_MESSAGE, error.getMessage());
        assertEquals(Constants.INVALID_JSON_DETAILS, error.getDetails());
    }

    @Test
    void shouldHandleExceptionDuringErrorLogging() {
        doThrow(new RuntimeException("DB offline"))
                .when(auditLogger)
                .logError(any(), any(), any(), any(), any(), any(), any());

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException("test exception")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(auditLogger).logError(any(), any(), any(), any(), any(), any(), any());
    }
}