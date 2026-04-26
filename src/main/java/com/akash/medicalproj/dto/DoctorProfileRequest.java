package com.akash.medicalproj.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DoctorProfileRequest {
    @NotBlank private String specialization;
    private String qualification;
    private String hospital;
    private String location;
    private BigDecimal consultationFee;
    private Integer experienceYears;
    private String bio;
    private Boolean isAvailable;
}
