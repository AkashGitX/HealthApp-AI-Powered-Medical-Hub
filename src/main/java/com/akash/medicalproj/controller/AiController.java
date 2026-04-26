package com.akash.medicalproj.controller;

import com.akash.medicalproj.dto.AiAnalysisResponse;
import com.akash.medicalproj.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;


    @GetMapping("/services/report-analyzer")
    public String reportAnalyzerPage() {
        return "services/report-analyzer";
    }

    @GetMapping("/services/medicine-finder")
    public String medicinefinderPage() {
        return "services/medicine-finder";
    }




    @PostMapping("/api/ai/analyze-report")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AiAnalysisResponse> analyzeReport(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(aiService.analyzeReport(file));
    }

    /** Medicine Finder — FIXED prompt, no user-controlled injection */
    @PostMapping("/api/ai/suggest-medicines")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AiAnalysisResponse> suggestMedicines(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(aiService.suggestCheaperMedicines(file));
    }
}
