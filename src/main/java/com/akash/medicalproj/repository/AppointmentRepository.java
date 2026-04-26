package com.akash.medicalproj.repository;

import com.akash.medicalproj.entity.Appointment;
import com.akash.medicalproj.entity.Appointment.AppointmentStatus;
import com.akash.medicalproj.entity.Doctor;
import com.akash.medicalproj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUserOrderByCreatedAtDesc(User user);
    List<Appointment> findByDoctorOrderByCreatedAtDesc(Doctor doctor);
    List<Appointment> findByDoctorAndStatus(Doctor doctor, AppointmentStatus status);
}
