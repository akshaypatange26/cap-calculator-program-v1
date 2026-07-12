package com.calculator.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "CALCULATION_HISTORY")
@Data
public class CalculationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_seq_gen")
    @SequenceGenerator(name = "history_seq_gen", sequenceName = "CALCULATION_HISTORY_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "MESSAGE_ID", nullable = false)
    private String messageId;

    @Column(name = "CORRELATION_ID", nullable = false)
    private String correlationId;

    @Column(name = "CONSUMER_TYPE", nullable = false)
    private String consumerType;

    @Column(name = "CLIENT_ID", nullable = false)
    private String clientId;

    @Column(name = "OPERAND1", nullable = false)
    private Double operand1;

    @Column(name = "OPERAND2", nullable = false)
    private Double operand2;

    @Column(name = "OPERATION", nullable = false)
    private String operation;

    @Column(name = "RESULT_VALUE", nullable = false)
    private Double resultValue;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
