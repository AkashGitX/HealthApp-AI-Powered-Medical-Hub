package com.akash.medicalproj.controller;

import com.akash.medicalproj.dto.ApiResponse;
import com.akash.medicalproj.dto.ServiceProviderDTO;
import com.akash.medicalproj.dto.ServiceProviderRequest;
import com.akash.medicalproj.entity.User;
import com.akash.medicalproj.service.ServiceProviderService;
import com.akash.medicalproj.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provider")
@RequiredArgsConstructor
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ServiceProviderDTO>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.ok(serviceProviderService.getAllActiveProviders(), "Success"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceProviderDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(serviceProviderService.getProviderById(id), "Success"));
    }

    @PostMapping("/profile")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<ApiResponse<ServiceProviderDTO>> createOrUpdate(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ServiceProviderRequest request) {

        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.ok(serviceProviderService.createOrUpdateProfile(user.getId(), request),
                        "Profile saved"));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<ApiResponse<ServiceProviderDTO>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.ok(serviceProviderService.getProviderByUserId(user.getId()), "Success"));
    }
}
