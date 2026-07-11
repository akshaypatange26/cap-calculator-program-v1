package com.calculator.api.utility;


public final class Constants {


    public static final String HEALTH_STATUS_MESSAGE = "Calculator service is running";

    // ============================
    // HTTP 500 ERROR CODES
    // ============================
    public static final String PROCESSING_ERROR_CODE = "10003";

    // ============================
    // ERROR MESSAGES
    // ============================
    public static final String INVALID_BODY_MESSAGE = "Invalid Body Parameter";
    public static final String INVALID_OPERATION_MESSAGE = "Invalid Operation";
    public static final String PROCESSING_ERROR_MESSAGE = "Error in Processing";

    // ============================
    // ERROR DETAILS
    // ============================
    public static final String INVALID_JSON_DETAILS = "Invalid JSON request";
    public static final String DIVISION_BY_ZERO_DETAILS = "Cannot divide by zero. operand2 must not be 0";
    public static final String NULL_REQUEST_DETAILS = "Calculation request cannot be null";
    public static final String NULL_OPERANDS_DETAILS = "Operands are required";
    public static final String OPERAND1_REQUIRED_DETAILS = "operand1 is required";
    public static final String OPERAND2_REQUIRED_DETAILS = "operand2 is required";
    public static final String OPERATION_REQUIRED_DETAILS = "operation is required";

    // ============================
    // BUSINESS VALIDATIONS
    // ============================
    public static final String INVALID_OPERATION = "InvalidOperationException";

    // ============================
    // JSON/JACKSON VALIDATION
    // ============================
    public static final String SUPPORTED_OPERATIONS_MESSAGE = "Supported operations are add, subtract, multiply and divide";
    public static final String INVALID_BODY = "Invalid Body Parameter";

    // ============================
    // GENERIC
    // ============================
    private Constants() {
        // Prevent object creation
    }

}