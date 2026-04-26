package com.akash.medicalproj.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceProviderDTO {
    private Long id;
    private Long userId;
    private String ownerName;
    private String shopName;
    private String address;
    private String city;
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
    private Boolean isActive;
}
