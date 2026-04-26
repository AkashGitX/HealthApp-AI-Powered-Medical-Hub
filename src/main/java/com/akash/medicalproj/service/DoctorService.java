package com.akash.medicalproj.service;

import com.akash.medicalproj.dto.DoctorDTO;
import com.akash.medicalproj.dto.DoctorProfileRequest;

import java.util.List;

public interface DoctorService {
    List<DoctorDTO> getAllAvailableDoctors();
    List<DoctorDTO> getOnlineDoctors();
    List<DoctorDTO> getOfflineDoctors();
    DoctorDTO getDoctorById(Long id);
    DoctorDTO getDoctorByUserId(Long userId);
    DoctorDTO createOrUpdateProfile(Long userId, DoctorProfileRequest request);
    DoctorDTO toggleAvailability(Long doctorId);
}
