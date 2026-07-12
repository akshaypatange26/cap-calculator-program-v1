package com.calculator.api.config;


import com.calculator.api.exception.InvalidHeaderException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


class HeaderValidationInterceptorTest {


    private final HeaderValidationInterceptor interceptor =
            new HeaderValidationInterceptor();


    private final HttpServletResponse response =
            new MockHttpServletResponse();


    @Test
    void shouldPassWhenAllHeadersAreValid() {


        MockHttpServletRequest request =
                new MockHttpServletRequest();


        addValidHeaders(request);


        assertDoesNotThrow(() ->
                interceptor.preHandle(
                        request,
                        response,
                        null
                )
        );
    }


    @Test
    void shouldThrowExceptionWhenHeadersAreMissing() {


        MockHttpServletRequest request =
                new MockHttpServletRequest();


        assertThrows(
                InvalidHeaderException.class,
                () ->
                        interceptor.preHandle(
                                request,
                                response,
                                null
                        )
        );
    }


    @Test
    void shouldThrowExceptionForInvalidConsumerType() {


        MockHttpServletRequest request =
                new MockHttpServletRequest();


        addValidHeaders(request);


        request.removeHeader(
                "x-consumerType"
        );


        request.addHeader(
                "x-consumerType",
                "INVALID_TYPE"
        );


        assertThrows(
                InvalidHeaderException.class,
                () ->
                        interceptor.preHandle(
                                request,
                                response,
                                null
                        )
        );
    }


    @Test
    void shouldThrowExceptionForInvalidMessageId() {


        MockHttpServletRequest request =
                new MockHttpServletRequest();


        addValidHeaders(request);


        request.removeHeader(
                "x-messageId"
        );


        request.addHeader(
                "x-messageId",
                "ABC123"
        );


        assertThrows(
                InvalidHeaderException.class,
                () ->
                        interceptor.preHandle(
                                request,
                                response,
                                null
                        )
        );
    }


    @Test
    void shouldThrowExceptionForInvalidCorrelationId() {


        MockHttpServletRequest request =
                new MockHttpServletRequest();


        addValidHeaders(request);


        request.removeHeader(
                "x-appCorrelationId"
        );


        request.addHeader(
                "x-appCorrelationId",
                "INVALID_UUID"
        );


        assertThrows(
                InvalidHeaderException.class,
                () ->
                        interceptor.preHandle(
                                request,
                                response,
                                null
                        )
        );
    }


    @Test
    void shouldThrowExceptionForInvalidClientId() {


        MockHttpServletRequest request =
                new MockHttpServletRequest();


        addValidHeaders(request);


        request.removeHeader(
                "x-client-id"
        );


        request.addHeader(
                "x-client-id",
                "@@@"
        );


        assertThrows(
                InvalidHeaderException.class,
                () ->
                        interceptor.preHandle(
                                request,
                                response,
                                null
                        )
        );
    }


    private void addValidHeaders(
            MockHttpServletRequest request) {


        request.addHeader(
                "x-messageId",
                "MSG-550e8400-e29b-41d4-a716-446655440000"
        );


        request.addHeader(
                "x-appCorrelationId",
                "550e8400-e29b-41d4-a716-446655440000"
        );


        request.addHeader(
                "x-consumerType",
                "WEB_APP"
        );


        request.addHeader(
                "x-client-id",
                "calculator-ui"
        );
    }
}