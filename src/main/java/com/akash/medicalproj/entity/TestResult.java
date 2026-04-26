package com.akash.medicalproj.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestType testType;

    private Integer score;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(columnDefinition = "TEXT")
    private String resultSummary;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Column(nullable = false, updatable = false)
    private LocalDateTime takenAt;

    @PrePersist
    protected void onCreate() {
        takenAt = LocalDateTime.now();
    }

    public enum TestType {
        MENTAL_HEALTH, EYE_CHECKUP
    }

    public enum RiskLevel {
        LOW, MEDIUM, HIGH
    }
}
