package com.akash.medicalproj.service;

import com.akash.medicalproj.dto.TestResultDTO;
import com.akash.medicalproj.entity.TestResult.RiskLevel;

import java.util.List;

public interface TestResultService {
    TestResultDTO saveMentalHealthResult(Long userId, int score);
    TestResultDTO saveEyeCheckupResult(Long userId, String riskLevel, String summary, String recommendation);
    List<TestResultDTO> getUserResults(Long userId);
    RiskLevel calculateRiskLevel(int score);
}
