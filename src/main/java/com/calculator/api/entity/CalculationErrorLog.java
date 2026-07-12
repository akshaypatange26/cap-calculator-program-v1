package com.calculator.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "CALCULATION_ERROR_LOG")
@Data
public class CalculationErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "error_seq_gen")
    @SequenceGenerator(name = "error_seq_gen", sequenceName = "CALCULATION_ERROR_LOG_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "MESSAGE_ID")
    private String messageId;

    @Column(name = "CORRELATION_ID")
    private String correlationId;

    @Column(name = "CONSUMER_TYPE")
    private String consumerType;

    @Column(name = "CLIENT_ID")
    private String clientId;

    @Column(name = "ERROR_CODE", nullable = false)
    private String errorCode;

    @Column(name = "ERROR_MESSAGE", nullable = false)
    private String errorMessage;

    @Column(name = "ERROR_DETAILS")
    private String errorDetails;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
