package com.akash.medicalproj.dto;

import com.akash.medicalproj.entity.Doctor.DoctorType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DoctorDTO {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String specialization;
    private String qualification;
    private String hospital;
    private String location;
    private BigDecimal consultationFee;
    private Integer experienceYears;
    private String bio;
    private String profileImageUrl;
    private Boolean isAvailable;
    private DoctorType doctorType;
}
