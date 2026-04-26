package com.akash.medicalproj.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ServiceProviderRequest {
    @NotBlank private String shopName;
    private String address;
    @NotBlank private String city;
    private String state;
    private String pincode;
    private String phone;
    private String email;
    private String availableDays;
    private String openTime;
    private String closeTime;
    private String availableDoctors;
    private String servicesOffered;
    private String pricingInfo;
}
