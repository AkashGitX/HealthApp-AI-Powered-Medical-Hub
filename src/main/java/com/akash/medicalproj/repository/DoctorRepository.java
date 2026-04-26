package com.akash.medicalproj.repository;

import com.akash.medicalproj.entity.Doctor;
import com.akash.medicalproj.entity.Doctor.DoctorType;
import com.akash.medicalproj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser(User user);
    Optional<Doctor> findByUserId(Long userId);
    List<Doctor> findByIsAvailableTrue();
    List<Doctor> findByIsAvailableTrueAndDoctorType(DoctorType doctorType);
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);
}
