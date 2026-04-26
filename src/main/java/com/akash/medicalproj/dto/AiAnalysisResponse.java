package com.akash.medicalproj.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiAnalysisResponse {
    private String explanation;
    private String type;
    private boolean success;
    private String errorMessage;
    private List<MedicineDTO> medicines;
}
