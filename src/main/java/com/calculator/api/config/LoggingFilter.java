package com.calculator.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Enumeration;

/**
 * Global logging filter aligned with production Splunk log schema.
 * Automatically captures full JSON request and response bodies, as well as metadata.
 */
@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // Wrap request and response to enable body caching
        ContentCachingRequestWrapper requestWrapper = request instanceof ContentCachingRequestWrapper ?
                (ContentCachingRequestWrapper) request : new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = response instanceof ContentCachingResponseWrapper ?
                (ContentCachingResponseWrapper) response : new ContentCachingResponseWrapper(response);

        // 1. Map headers to match your Splunk production schema
        String correlationId = requestWrapper.getHeader("x-appCorrelationId");
        if (correlationId != null && !correlationId.isBlank()) {
            MDC.put("app_correlation_id", correlationId);
        }

        String messageId = requestWrapper.getHeader("x-messageId");
        if (messageId != null && !messageId.isBlank()) {
            MDC.put("message_id", messageId);
        }

        String consumerType = requestWrapper.getHeader("x-consumerType");
        if (consumerType != null && !consumerType.isBlank()) {
            MDC.put("consumer_id", consumerType);
        }

        String clientId = requestWrapper.getHeader("x-client-id");
        if (clientId != null && !clientId.isBlank()) {
            MDC.put("originating_system_id", clientId);
        }

        // Hardcode category and environment/app fields for standard mapping
        MDC.put("log_category", "API");
        MDC.put("appid", "A008E0");
        MDC.put("component_name", "calculator-api-v1");
        MDC.put("class_name", this.getClass().getName());

        // Capture all raw headers with standard prefix for troubleshooting
        Enumeration<String> headerNames = requestWrapper.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = requestWrapper.getHeader(headerName);
                MDC.put("Header_" + headerName, headerValue);
            }
        }

        // 2. Log request details
        MDC.put("request_method", requestWrapper.getMethod());
        MDC.put("request", requestWrapper.getMethod() + " " + requestWrapper.getRequestURI());
        MDC.put("remote_addr", requestWrapper.getRemoteAddr());

        log.info("Incoming Request: {} {} from IP: {}", requestWrapper.getMethod(), requestWrapper.getRequestURI(), requestWrapper.getRemoteAddr());

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // 3. Extract request and response bodies
            String requestBody = getRequestBody(requestWrapper);
            String responseBody = getResponseBody(responseWrapper);

            if (requestBody != null && !requestBody.isBlank()) {
                MDC.put("request_body", requestBody);
            }
            if (responseBody != null && !responseBody.isBlank()) {
                MDC.put("response_body", responseBody);
            }

            // 4. Capture response telemetry and format log message
            long duration = System.currentTimeMillis() - startTime;
            int status = responseWrapper.getStatus();

            MDC.put("status", String.valueOf(status));
            MDC.put("request_time", String.valueOf(duration));

            // Format message style matching Splunk screenshot + request/response body inline:
            // "Method Execution Time taken :: @@@ [METHOD] [URI] @@@ ELAPSEDMS=[duration] ms @@@ ReqBody: [body] | RespBody: [body]"
            String safeReqBody = (requestBody != null && !requestBody.isBlank()) ? requestBody.trim() : "{}";
            String safeRespBody = (responseBody != null && !responseBody.isBlank()) ? responseBody.trim() : "{}";

            String messageTemplate = "Method Execution Time taken :: @@@ {} {} @@@ ELAPSEDMS={} ms @@@ ReqBody: {} | RespBody: {}";

            if (status >= 400) {
                log.warn("Method Execution Time taken :: @@@ {} {} @@@ ELAPSEDMS={} ms @@@ ReqBody: {} | RespBody: {} (Status: {})",
                        requestWrapper.getMethod(), requestWrapper.getRequestURI(), duration, safeReqBody, safeRespBody, status);
            } else {
                log.info(messageTemplate, requestWrapper.getMethod(), requestWrapper.getRequestURI(), duration, safeReqBody, safeRespBody);
            }

            // 5. CRITICAL: Copy cached response body back to the actual response output stream
            responseWrapper.copyBodyToResponse();

            // 6. Clean up MDC context on completion
            MDC.clear();
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                String encoding = request.getCharacterEncoding();
                return new String(buf, 0, buf.length, encoding != null ? encoding : "UTF-8");
            } catch (Exception e) {
                return "[Unsupported Request Encoding]";
            }
        }
        return null;
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                String encoding = response.getCharacterEncoding();
                return new String(buf, 0, buf.length, encoding != null ? encoding : "UTF-8");
            } catch (Exception e) {
                return "[Unsupported Response Encoding]";
            }
        }
        return null;
    }
}
