package com.akash.medicalproj.service;

import com.akash.medicalproj.dto.AiAnalysisResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AiService {
    AiAnalysisResponse analyzeReport(MultipartFile file);
    AiAnalysisResponse suggestCheaperMedicines(MultipartFile prescription);
}
