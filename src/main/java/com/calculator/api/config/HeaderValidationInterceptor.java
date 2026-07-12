package com.calculator.api.config;

import com.calculator.api.exception.InvalidHeaderException;
import com.calculator.api.utility.Constants;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

import static com.calculator.api.utility.Constants.*;

@Component
public class HeaderValidationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {

        List<String> errors = new ArrayList<>();
        validateRequiredHeaders(request, errors);
        validateMessageId(request, errors);
        validateCorrelationId(request, errors);
        validateConsumerType(request, errors);
        validateClientId(request, errors);
        if (!errors.isEmpty()) {
            throw new InvalidHeaderException(errors);
        }
        return true;
    }

    private void validateRequiredHeaders(@Nonnull HttpServletRequest request, @Nonnull List<String> errors) {

        Constants.REQUIRED_HEADERS.forEach(header -> {
            String value = request.getHeader(header);
            if (value == null || value.isBlank()) {
                errors.add(Constants.INVALID_HEADER_FORMAT_DETAILS + header);
            }
        });
    }

    private void validateMessageId(@Nonnull HttpServletRequest request, @Nonnull List<String> errors) {

        String value = request.getHeader(Constants.MESSAGE_ID_HEADER);
        if (value != null && !MESSAGE_ID_PATTERN.matcher(value).matches()) {
            errors.add(Constants.INVALID_HEADER_FORMAT_DETAILS + Constants.MESSAGE_ID_HEADER);
        }
    }

    private void validateCorrelationId(@Nonnull HttpServletRequest request, @Nonnull List<String> errors) {
        String value = request.getHeader(Constants.CORRELATION_ID_HEADER);
        if (value != null && !UUID_PATTERN.matcher(value).matches()) {
            errors.add(Constants.INVALID_HEADER_FORMAT_DETAILS + Constants.CORRELATION_ID_HEADER);
        }
    }

    private void validateConsumerType(@Nonnull HttpServletRequest request, @Nonnull List<String> errors) {

        String value = request.getHeader(Constants.CONSUMER_TYPE_HEADER);
        if (value != null && !Constants.ALLOWED_CONSUMER_TYPES.contains(value)) {
            errors.add(Constants.INVALID_HEADER_FORMAT_DETAILS + Constants.CONSUMER_TYPE_HEADER + Constants.SUPPORTED_CONSUMER_TYPES_DETAILS + String.join(", ", Constants.ALLOWED_CONSUMER_TYPES));
        }
    }

    private void validateClientId(@Nonnull HttpServletRequest request, @Nonnull List<String> errors) {

        String value = request.getHeader(Constants.CLIENT_ID_HEADER);
        if (value != null && !CLIENT_ID_PATTERN.matcher(value).matches()) {
            errors.add(Constants.INVALID_HEADER_FORMAT_DETAILS + Constants.CLIENT_ID_HEADER);
        }
    }
}