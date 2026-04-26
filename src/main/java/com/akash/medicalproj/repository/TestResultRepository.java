package com.akash.medicalproj.repository;

import com.akash.medicalproj.entity.TestResult;
import com.akash.medicalproj.entity.TestResult.TestType;
import com.akash.medicalproj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findByUserOrderByTakenAtDesc(User user);
    List<TestResult> findByUserAndTestType(User user, TestType testType);
}
