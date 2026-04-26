package com.akash.medicalproj.service.impl;

import com.akash.medicalproj.dto.DoctorDTO;
import com.akash.medicalproj.dto.DoctorProfileRequest;
import com.akash.medicalproj.entity.Doctor;
import com.akash.medicalproj.entity.Doctor.DoctorType;
import com.akash.medicalproj.entity.User;
import com.akash.medicalproj.exception.ResourceNotFoundException;
import com.akash.medicalproj.repository.DoctorRepository;
import com.akash.medicalproj.repository.UserRepository;
import com.akash.medicalproj.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Override
    public List<DoctorDTO> getAllAvailableDoctors() {
        return doctorRepository.findByIsAvailableTrue()
                .stream().map(this::toDTO).toList();
    }

    @Override
    public List<DoctorDTO> getOnlineDoctors() {
        return doctorRepository.findByIsAvailableTrueAndDoctorType(DoctorType.ONLINE)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public List<DoctorDTO> getOfflineDoctors() {
        return doctorRepository.findByIsAvailableTrueAndDoctorType(DoctorType.OFFLINE)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public DoctorDTO getDoctorById(Long id) {
        return toDTO(doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id)));
    }

    @Override
    public DoctorDTO getDoctorByUserId(Long userId) {
        return toDTO(doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found")));
    }

    @Override
    public DoctorDTO createOrUpdateProfile(Long userId, DoctorProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Doctor doctor = doctorRepository.findByUserId(userId).orElse(
                Doctor.builder().user(user).build()
        );

        doctor.setSpecialization(request.getSpecialization());
        doctor.setQualification(request.getQualification());
        doctor.setHospital(request.getHospital());
        doctor.setLocation(request.getLocation());
        doctor.setConsultationFee(request.getConsultationFee());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setBio(request.getBio());
        if (request.getIsAvailable() != null) doctor.setIsAvailable(request.getIsAvailable());

        return toDTO(doctorRepository.save(doctor));
    }

    @Override
    public DoctorDTO toggleAvailability(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", doctorId));
        doctor.setIsAvailable(!doctor.getIsAvailable());
        return toDTO(doctorRepository.save(doctor));
    }

    private DoctorDTO toDTO(Doctor d) {
        return DoctorDTO.builder()
                .id(d.getId())
                .userId(d.getUser().getId())
                .fullName(d.getUser().getFullName())
                .email(d.getUser().getEmail())
                .specialization(d.getSpecialization())
                .qualification(d.getQualification())
                .hospital(d.getHospital())
                .location(d.getLocation())
                .consultationFee(d.getConsultationFee())
                .experienceYears(d.getExperienceYears())
                .bio(d.getBio())
                .profileImageUrl(d.getProfileImageUrl())
                .isAvailable(d.getIsAvailable())
                .doctorType(d.getDoctorType() != null ? d.getDoctorType() : Doctor.DoctorType.ONLINE)
                .build();
    }
}
