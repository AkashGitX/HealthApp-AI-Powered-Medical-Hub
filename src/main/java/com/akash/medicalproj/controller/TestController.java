package com.akash.medicalproj.controller;

import com.akash.medicalproj.dto.ApiResponse;
import com.akash.medicalproj.dto.TestResultDTO;
import com.akash.medicalproj.entity.User;
import com.akash.medicalproj.service.TestResultService;
import com.akash.medicalproj.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TestController {

    private final TestResultService testResultService;
    private final UserService userService;

    // ── Pages ──────────────────────────────────────────────────
    @GetMapping("/services/mental-health-test")
    public String mentalHealthPage() {
        return "services/mental-health-test";
    }

    @GetMapping("/services/eye-checkup")
    public String eyeCheckupPage() {
        return "services/eye-checkup";
    }

    // ── REST: get user's test results ──────────────────────────
    @GetMapping("/api/tests/my")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<java.util.List<TestResultDTO>>> getMyResults(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(testResultService.getUserResults(user.getId()), "Success"));
    }

    // ── REST: submit mental health test score ──────────────────
    @PostMapping("/api/tests/mental-health")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TestResultDTO>> submitMentalHealthTest(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int score) {

        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        TestResultDTO result = testResultService.saveMentalHealthResult(user.getId(), score);
        return ResponseEntity.ok(ApiResponse.ok(result, "Test result saved"));
    }

    // ── REST: submit eye checkup result ────────────────────────
    @PostMapping("/api/tests/eye-checkup")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TestResultDTO>> submitEyeCheckup(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String riskLevel,
            @RequestParam String summary,
            @RequestParam(required = false, defaultValue = "") String recommendation) {

        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        TestResultDTO result = testResultService.saveEyeCheckupResult(
                user.getId(), riskLevel, summary, recommendation);
        return ResponseEntity.ok(ApiResponse.ok(result, "Eye checkup result saved"));
    }
}
