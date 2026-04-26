package com.akash.medicalproj.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotNull private Long appointmentId;
    @NotNull private BigDecimal amount;
    private String currency;
}
