package com.calculator.utility;

public final class Constants {


    private Constants() {
        // Prevent object creation
    }

    // Health API
    public static final String HEALTH_STATUS_MESSAGE = "Calculator service is running";

    // Validation Error Codes
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String INVALID_JSON = "INVALID_JSON";
    public static final String INVALID_OPERATION = "INVALID_OPERATION";
    public static final String UNSUPPORTED_OPERATION_MESSAGE = "Unsupported operation";
    public static final String OPERATION_ENUM_MESSAGE = "OperationEnum";
    public static final String VALIDATION_FAILED_MESSAGE = "Validation failed";
    public static final String NULL_CALCULATION_REQUEST_MESSAGE = "Calculation request cannot be null";
    public static final String OPERAND2_REQUIRED_MESSAGE = "operand2 is required";

    // Business Error Codes
    public static final String DIVISION_BY_ZERO = "DIVISION_BY_ZERO";

    // System Error Codes
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String INVALID_REQUEST = "INVALID_REQUEST";

    // Error Messages
    public static final String DIVISION_BY_ZERO_MESSAGE = "Cannot divide by zero. operand2 must not be 0";
    public static final String SUPPORTED_OPERATIONS_MESSAGE = "Invalid operation. Supported operations are: add, subtract, multiply, divide";
    public static final String INVALID_JSON_MESSAGE = "Invalid JSON request";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";
    public static final String UNEXPECTED_ERROR_MESSAGE = "Unexpected error occurred";
}