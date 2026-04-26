package com.akash.medicalproj.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicineDTO {
    private String name;
    private String genericName;
    private String price;
    private String amazonLink;
    private String flipkartLink;
    private String oneMgLink;
    private String pharmEasyLink;
}
