package com.calculator.api.utility;

import java.util.List;
import java.util.regex.Pattern;

public final class Constants {

    // ==========================
    // Health
    // ==========================
    public static final String HEALTH_STATUS_MESSAGE = "Calculator service is running";
    // ==========================
    // Error Codes (OpenAPI)
    // ==========================
    public static final String INVALID_HEADER_CODE = "1001";
    public static final String INVALID_BODY_CODE = "1002";
    public static final String INVALID_OPERATION_CODE = "1003";
    public static final String PROCESSING_ERROR_CODE = "10003";
    // ==========================
    // Error Messages
    // ==========================
    public static final String INVALID_HEADER_MESSAGE = "Invalid Header Parameter";
    public static final String INVALID_BODY_MESSAGE = "Invalid Body Parameter";
    public static final String INVALID_OPERATION_MESSAGE = "Invalid Operation";
    public static final String PROCESSING_ERROR_MESSAGE = "Error in Processing";
    public static final String MESSAGE_ID_HEADER = "x-messageId";
    public static final String CORRELATION_ID_HEADER = "x-appCorrelationId";
    public static final String CONSUMER_TYPE_HEADER = "x-consumerType";
    public static final String CLIENT_ID_HEADER = "x-client-id";

    // ==========================
    // Details
    // ==========================
    public static final String INVALID_JSON_DETAILS = "Invalid JSON request";
    public static final String NULL_REQUEST_DETAILS = "Calculation request cannot be null";
    public static final String NULL_OPERANDS_DETAILS = "Operands are required";
    public static final String OPERAND1_REQUIRED_DETAILS = "operand1 is required";
    public static final String OPERAND2_REQUIRED_DETAILS = "operand2 is required";
    public static final String OPERATION_REQUIRED_DETAILS = "operation is required";
    public static final String DIVISION_BY_ZERO_DETAILS = "Cannot divide by zero. operand2 must not be 0";
    public static final String SUPPORTED_OPERATIONS_MESSAGE = "Supported operations are add, subtract, multiply and divide";
    public static final String INVALID_HEADER_FORMAT_DETAILS = "Invalid format for header: ";
    public static final String SUPPORTED_CONSUMER_TYPES_DETAILS = ". Supported values are ";
    public static final Pattern MESSAGE_ID_PATTERN = Pattern.compile("^MSG-[0-9a-fA-F-]{36}$");
    public static final Pattern CLIENT_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{3,50}$");
    public static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-" +
                    "[0-9a-fA-F]{4}-" +
                    "[1-5][0-9a-fA-F]{3}-" +
                    "[89abAB][0-9a-fA-F]{3}-" +
                    "[0-9a-fA-F]{12}$"
    );
    public static final List<String> ALLOWED_CONSUMER_TYPES = List.of("MOBILE_APP", "WEB_APP", "INTERNAL_SYSTEM");
    public static final List<String> REQUIRED_HEADERS = List.of(Constants.MESSAGE_ID_HEADER, Constants.CORRELATION_ID_HEADER, Constants.CONSUMER_TYPE_HEADER, Constants.CLIENT_ID_HEADER);
    public static final String[] PATH_PATTERNS = {"/calculator/**"};
    public static final String UNEXPECTED_VALUE_DETAILS = "Unexpected value";
    public static final String MISSING_HEADER_DETAILS = "Required header parameter is missing: ";

    private Constants() {
    }
}