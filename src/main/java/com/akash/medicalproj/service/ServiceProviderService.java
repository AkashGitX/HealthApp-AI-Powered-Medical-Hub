package com.akash.medicalproj.service;

import com.akash.medicalproj.dto.ServiceProviderDTO;
import com.akash.medicalproj.dto.ServiceProviderRequest;

import java.util.List;

public interface ServiceProviderService {
    List<ServiceProviderDTO> getAllActiveProviders();
    ServiceProviderDTO getProviderById(Long id);
    ServiceProviderDTO getProviderByUserId(Long userId);
    ServiceProviderDTO createOrUpdateProfile(Long userId, ServiceProviderRequest request);
}
