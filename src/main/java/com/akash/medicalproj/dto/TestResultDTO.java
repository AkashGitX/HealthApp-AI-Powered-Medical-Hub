package com.akash.medicalproj.dto;

import com.akash.medicalproj.entity.TestResult.RiskLevel;
import com.akash.medicalproj.entity.TestResult.TestType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TestResultDTO {
    private Long id;
    private Long userId;
    private String userName;
    private TestType testType;
    private Integer score;
    private RiskLevel riskLevel;
    private String resultSummary;
    private String recommendation;
    private LocalDateTime takenAt;
}
