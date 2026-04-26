package com.akash.medicalproj.controller;

import com.akash.medicalproj.dto.ApiResponse;
import com.akash.medicalproj.dto.DoctorDTO;
import com.akash.medicalproj.dto.DoctorProfileRequest;
import com.akash.medicalproj.entity.User;
import com.akash.medicalproj.service.DoctorService;
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
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAllDoctors() {
        return ResponseEntity.ok(ApiResponse.ok(doctorService.getAllAvailableDoctors(), "Success"));
    }

    @GetMapping("/online")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getOnlineDoctors() {
        return ResponseEntity.ok(ApiResponse.ok(doctorService.getOnlineDoctors(), "Success"));
    }

    @GetMapping("/offline")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getOfflineDoctors() {
        return ResponseEntity.ok(ApiResponse.ok(doctorService.getOfflineDoctors(), "Success"));
    }

    @GetMapping("/my-profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorDTO>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getUserEntityByEmail(userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.ok(doctorService.getDoctorByUserId(user.getId()), "Success"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.<DoctorDTO>builder()
                    .success(false).message("No profile yet").build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorDTO>> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(doctorService.getDoctorById(id), "Success"));
    }

    @PostMapping("/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorDTO>> createOrUpdateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DoctorProfileRequest request) {

        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.ok(doctorService.createOrUpdateProfile(user.getId(), request), "Profile saved"));
    }

    @PatchMapping("/availability")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorDTO>> toggleAvailability(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        var doctor = doctorService.getDoctorByUserId(user.getId());
        return ResponseEntity.ok(
                ApiResponse.ok(doctorService.toggleAvailability(doctor.getId()), "Availability updated"));
    }
}
