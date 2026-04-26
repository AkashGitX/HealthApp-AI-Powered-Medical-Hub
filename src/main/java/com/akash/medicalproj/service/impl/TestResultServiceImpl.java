package com.akash.medicalproj.service.impl;

import com.akash.medicalproj.dto.TestResultDTO;
import com.akash.medicalproj.entity.TestResult;
import com.akash.medicalproj.entity.TestResult.RiskLevel;
import com.akash.medicalproj.entity.TestResult.TestType;
import com.akash.medicalproj.entity.User;
import com.akash.medicalproj.exception.ResourceNotFoundException;
import com.akash.medicalproj.repository.TestResultRepository;
import com.akash.medicalproj.repository.UserRepository;
import com.akash.medicalproj.service.TestResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TestResultServiceImpl implements TestResultService {

    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;

    @Override
    public TestResultDTO saveMentalHealthResult(Long userId, int score) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        RiskLevel riskLevel = calculateRiskLevel(score);
        String summary = buildSummary(score, riskLevel);
        String recommendation = buildRecommendation(riskLevel);

        TestResult result = TestResult.builder()
                .user(user)
                .testType(TestType.MENTAL_HEALTH)
                .score(score)
                .riskLevel(riskLevel)
                .resultSummary(summary)
                .recommendation(recommendation)
                .build();

        return toDTO(testResultRepository.save(result));
    }

    @Override
    public TestResultDTO saveEyeCheckupResult(Long userId, String riskLevel, String summary, String recommendation) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        RiskLevel level;
        try {
            level = RiskLevel.valueOf(riskLevel.toUpperCase());
        } catch (Exception e) {
            level = RiskLevel.LOW;
        }

        TestResult result = TestResult.builder()
                .user(user)
                .testType(TestType.EYE_CHECKUP)
                .riskLevel(level)
                .resultSummary(summary)
                .recommendation(recommendation)
                .build();

        return toDTO(testResultRepository.save(result));
    }

    @Override
    public List<TestResultDTO> getUserResults(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return testResultRepository.findByUserOrderByTakenAtDesc(user)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public RiskLevel calculateRiskLevel(int score) {
        if (score < 30) return RiskLevel.LOW;
        if (score <= 70) return RiskLevel.MEDIUM;
        return RiskLevel.HIGH;
    }

    private String buildSummary(int score, RiskLevel level) {
        return switch (level) {
            case LOW    -> "Your mental health score is " + score + "/100. You appear to be in good mental health.";
            case MEDIUM -> "Your mental health score is " + score + "/100. You are experiencing moderate stress or anxiety.";
            case HIGH   -> "Your mental health score is " + score + "/100. You are showing signs of significant mental health challenges.";
        };
    }

    private String buildRecommendation(RiskLevel level) {
        return switch (level) {
            case LOW    -> "Keep up your healthy habits. Practice mindfulness and regular exercise.";
            case MEDIUM -> "Consider speaking with a counselor. Practice stress management techniques.";
            case HIGH   -> "We strongly recommend consulting a mental health professional as soon as possible.";
        };
    }

    private TestResultDTO toDTO(TestResult r) {
        return TestResultDTO.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .userName(r.getUser().getFullName())
                .testType(r.getTestType())
                .score(r.getScore())
                .riskLevel(r.getRiskLevel())
                .resultSummary(r.getResultSummary())
                .recommendation(r.getRecommendation())
                .takenAt(r.getTakenAt())
                .build();
    }
}
