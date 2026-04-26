package com.akash.medicalproj.service.impl;

import com.akash.medicalproj.dto.ServiceProviderDTO;
import com.akash.medicalproj.dto.ServiceProviderRequest;
import com.akash.medicalproj.entity.ServiceProvider;
import com.akash.medicalproj.entity.User;
import com.akash.medicalproj.exception.ResourceNotFoundException;
import com.akash.medicalproj.repository.ServiceProviderRepository;
import com.akash.medicalproj.repository.UserRepository;
import com.akash.medicalproj.service.ServiceProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ServiceProviderServiceImpl implements ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final UserRepository userRepository;

    @Override
    public List<ServiceProviderDTO> getAllActiveProviders() {
        return serviceProviderRepository.findByIsActiveTrue()
                .stream().map(this::toDTO).toList();
    }

    @Override
    public ServiceProviderDTO getProviderById(Long id) {
        return toDTO(serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProvider", id)));
    }

    @Override
    public ServiceProviderDTO getProviderByUserId(Long userId) {
        return toDTO(serviceProviderRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found")));
    }

    @Override
    public ServiceProviderDTO createOrUpdateProfile(Long userId, ServiceProviderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        ServiceProvider sp = serviceProviderRepository.findByUserId(userId).orElse(
                ServiceProvider.builder().user(user).build()
        );

        sp.setShopName(request.getShopName());
        sp.setAddress(request.getAddress());
        sp.setCity(request.getCity());
        sp.setState(request.getState());
        sp.setPincode(request.getPincode());
        sp.setPhone(request.getPhone());
        sp.setEmail(request.getEmail());
        sp.setAvailableDays(request.getAvailableDays());
        sp.setOpenTime(request.getOpenTime());
        sp.setCloseTime(request.getCloseTime());
        sp.setAvailableDoctors(request.getAvailableDoctors());
        sp.setServicesOffered(request.getServicesOffered());
        sp.setPricingInfo(request.getPricingInfo());

        return toDTO(serviceProviderRepository.save(sp));
    }

    private ServiceProviderDTO toDTO(ServiceProvider sp) {
        return ServiceProviderDTO.builder()
                .id(sp.getId())
                .userId(sp.getUser().getId())
                .ownerName(sp.getUser().getFullName())
                .shopName(sp.getShopName())
                .address(sp.getAddress())
                .city(sp.getCity())
                .state(sp.getState())
                .pincode(sp.getPincode())
                .phone(sp.getPhone())
                .email(sp.getEmail())
                .availableDays(sp.getAvailableDays())
                .openTime(sp.getOpenTime())
                .closeTime(sp.getCloseTime())
                .availableDoctors(sp.getAvailableDoctors())
                .servicesOffered(sp.getServicesOffered())
                .pricingInfo(sp.getPricingInfo())
                .isActive(sp.getIsActive())
                .build();
    }
}
