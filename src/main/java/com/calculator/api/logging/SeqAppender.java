package com.calculator.api.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

/**
 * Custom Logback appender that asynchronously forwards logs to a Seq server
 * using Seq's native Compact Log Event Format (CLEF).
 */
public class SeqAppender extends AppenderBase<ILoggingEvent> {

    private String serverUrl = "http://localhost:5341";
    private String apiKey;
    private HttpClient httpClient;

    @Override
    public void start() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(3))
                .build();
        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (event == null || httpClient == null) {
            return;
        }

        try {
            // Format log event into Compact Log Event Format (CLEF)
            StringBuilder json = new StringBuilder();
            json.append("{");
            
            // Core CLEF properties
            json.append("\"@t\":\"").append(Instant.ofEpochMilli(event.getTimeStamp()).toString()).append("\",");

            // Convert SLF4J '{}' placeholders to Seq '{0}', '{1}' for rich token rendering
            String messagePattern = event.getMessage();
            Object[] args = event.getArgumentArray();
            if (messagePattern != null && messagePattern.contains("{}") && args != null && args.length > 0) {
                StringBuilder templateBuilder = new StringBuilder();
                int paramIndex = 0;
                int lastIdx = 0;
                int idx;
                while ((idx = messagePattern.indexOf("{}", lastIdx)) != -1) {
                    templateBuilder.append(messagePattern, lastIdx, idx);
                    templateBuilder.append("{").append(paramIndex).append("}");
                    paramIndex++;
                    lastIdx = idx + 2;
                }
                templateBuilder.append(messagePattern, lastIdx, messagePattern.length());

                json.append("\"@mt\":").append(escapeJson(templateBuilder.toString())).append(",");
                
                // Append the argument values as numbered properties
                for (int i = 0; i < Math.min(args.length, paramIndex); i++) {
                    json.append("\"").append(i).append("\":").append(escapeJson(args[i] != null ? args[i].toString() : "null")).append(",");
                }
            } else {
                json.append("\"@m\":").append(escapeJson(event.getFormattedMessage())).append(",");
            }

            json.append("\"@l\":\"").append(mapLevel(event.getLevel().toString())).append("\",");
            
            // Custom application properties
            json.append("\"SourceContext\":").append(escapeJson(event.getLoggerName())).append(",");
            json.append("\"ThreadName\":").append(escapeJson(event.getThreadName()));

            // Add exception stack trace if present
            IThrowableProxy throwableProxy = event.getThrowableProxy();
            if (throwableProxy != null) {
                json.append(",\"@x\":").append(escapeJson(formatException(throwableProxy)));
            }

            // Include Mapped Diagnostic Context (MDC) properties if any exist
            Map<String, String> mdc = event.getMDCPropertyMap();
            if (mdc != null && !mdc.isEmpty()) {
                for (Map.Entry<String, String> entry : mdc.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        json.append(",\"").append(entry.getKey()).append("\":").append(escapeJson(entry.getValue()));
                    }
                }
            }
            json.append("}");

            // Prepare HTTP Post request to Seq native ingestion raw endpoint
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/api/events/raw"))
                    .header("Content-Type", "application/vnd.serilog.clef")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString(), StandardCharsets.UTF_8));

            if (apiKey != null && !apiKey.trim().isEmpty()) {
                requestBuilder.header("X-Seq-ApiKey", apiKey);
            }

            // Fire and forget asynchronously to avoid blocking the application threads
            httpClient.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.discarding());

        } catch (Exception e) {
            // Prevent recursive logging failure loops by only printing to standard error
            System.err.println("SeqAppender failed to forward log: " + e.getMessage());
        }
    }

    /**
     * Map Logback levels to Seq levels (Seq prefers full names or short standard forms)
     */
    private String mapLevel(String logbackLevel) {
        if ("WARN".equalsIgnoreCase(logbackLevel)) {
            return "Warning";
        } else if ("ERROR".equalsIgnoreCase(logbackLevel)) {
            return "Error";
        } else if ("DEBUG".equalsIgnoreCase(logbackLevel)) {
            return "Debug";
        } else if ("TRACE".equalsIgnoreCase(logbackLevel)) {
            return "Verbose";
        } else {
            return "Information";
        }
    }

    /**
     * Formats the exception stack trace as a readable string
     */
    private String formatException(IThrowableProxy throwableProxy) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwableProxy.getClassName()).append(": ").append(throwableProxy.getMessage()).append("\n");
        for (StackTraceElementProxy step : throwableProxy.getStackTraceElementProxyArray()) {
            sb.append("\tat ").append(step.getSTEAsString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Minimal JSON escaping to prevent malformed payloads
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '\\': sb.append("\\\\"); break;
                case '"': sb.append("\\\""); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (ch < ' ') {
                        String t = "000" + Integer.toHexString(ch);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(ch);
                    }
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    // Bean Setters for Logback configuration injection
    public void setServerUrl(String serverUrl) {
        if (serverUrl != null && !serverUrl.trim().isEmpty() && !serverUrl.startsWith("${")) {
            this.serverUrl = serverUrl;
        }
    }

    public void setApiKey(String apiKey) {
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.startsWith("${")) {
            this.apiKey = apiKey;
        }
    }
}
