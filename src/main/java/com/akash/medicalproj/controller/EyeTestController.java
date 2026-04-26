package com.akash.medicalproj.controller;

import com.akash.medicalproj.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eye-test")
@RequiredArgsConstructor
public class EyeTestController {

    // ── Visual Acuity ─────────────────────────────────────────────────────────
    @PostMapping("/visual")
    public ResponseEntity<ApiResponse<Map<String, String>>> evaluateVisualAcuity(
            @RequestParam String acuityRow) {

        String result, detail, level;
        switch (acuityRow) {
            case "20/20" -> { result = "Normal Vision (20/20)"; detail = "Your visual acuity is excellent. No correction needed."; level = "NORMAL"; }
            case "20/25" -> { result = "Near-Normal Vision (20/25)"; detail = "Your vision is slightly below perfect but generally considered normal."; level = "NORMAL"; }
            case "20/30" -> { result = "Mild Vision Reduction (20/30)"; detail = "Mild reduction detected. An optometrist check is recommended."; level = "MILD"; }
            case "20/50" -> { result = "Moderate Vision Loss (20/50)"; detail = "Glasses or contacts may be needed. Please see an eye specialist."; level = "MODERATE"; }
            case "20/70" -> { result = "Significant Vision Loss (20/70)"; detail = "Vision correction is strongly recommended. Consult an ophthalmologist."; level = "SIGNIFICANT"; }
            case "20/100" -> { result = "Severe Vision Loss (20/100)"; detail = "Please consult an eye specialist urgently."; level = "SEVERE"; }
            default -> { result = "Very Low Vision (20/200)"; detail = "Urgent eye specialist consultation recommended."; level = "SEVERE"; }
        }

        return ResponseEntity.ok(ApiResponse.ok(
                Map.of("acuityRow", acuityRow, "result", result, "detail", detail, "level", level),
                "Visual acuity evaluated"));
    }

    // ── Color Blindness ──────────────────────────────────────────────────────
    @PostMapping("/color")
    public ResponseEntity<ApiResponse<Map<String, Object>>> evaluateColorTest(
            @RequestBody List<String> answers) {

        // Correct answers for the 12 Ishihara plates (in plate order)
        String[] correct = {"74", "6", "16", "2", "29", "7", "45", "5", "97", "8", "42", "3"};

        if (answers == null || answers.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("No answers provided"));
        }

        int score = 0;
        int total = Math.min(answers.size(), correct.length);
        for (int i = 0; i < total; i++) {
            if (correct[i].equalsIgnoreCase(answers.get(i).trim())) score++;
        }

        double pct = total > 0 ? (double) score / total * 100 : 0;
        String diagnosis, detail, level;

        // 10–12 correct → Normal, 6–9 → Mild, 0–5 → Red-Green Color Blindness
        if (score >= 10) {
            diagnosis = "Normal Color Vision";
            detail = "You identified most plates correctly. No significant color vision deficiency detected.";
            level = "NORMAL";
        } else if (score >= 6) {
            diagnosis = "Mild Color Deficiency";
            detail = "You may have mild difficulty distinguishing certain colors. A professional test is recommended.";
            level = "MILD";
        } else {
            diagnosis = "Red-Green Color Blindness";
            detail = "Your results suggest red-green color blindness. Please consult an eye specialist for a professional assessment.";
            level = "DEFICIENT";
        }

        return ResponseEntity.ok(ApiResponse.ok(
                Map.of("score", score, "total", total, "percentage", Math.round(pct),
                        "diagnosis", diagnosis, "detail", detail, "level", level),
                "Color blindness test evaluated"));
    }

    // ── Amsler Grid ──────────────────────────────────────────────────────────
    @PostMapping("/amsler")
    public ResponseEntity<ApiResponse<Map<String, String>>> evaluateAmsler(
            @RequestParam String response) {

        String result, detail, level;
        switch (response) {
            case "normal" -> {
                result = "Amsler Grid Normal";
                detail = "The grid lines appear straight and complete. No signs of macular distortion.";
                level = "NORMAL";
            }
            case "slightly-wavy" -> {
                result = "Mild Distortion Detected";
                detail = "Slight waviness noted. Monitor your vision regularly and consider an eye exam if it worsens.";
                level = "MILD";
            }
            case "wavy" -> {
                result = "Significant Distortion Detected";
                detail = "Wavy or blurry lines may indicate early macular degeneration. Please consult an ophthalmologist.";
                level = "WARNING";
            }
            default -> {
                result = "Severe Distortion / Missing Areas";
                detail = "Missing areas or severe distortion detected. This may indicate macular degeneration — urgent consultation recommended.";
                level = "URGENT";
            }
        }

        return ResponseEntity.ok(ApiResponse.ok(
                Map.of("response", response, "result", result, "detail", detail, "level", level),
                "Amsler grid evaluated"));
    }
}
