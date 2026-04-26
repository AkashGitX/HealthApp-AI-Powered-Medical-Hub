package com.akash.medicalproj.service;

import com.akash.medicalproj.dto.AppointmentDTO;
import com.akash.medicalproj.dto.AppointmentRequest;
import com.akash.medicalproj.entity.Appointment.AppointmentStatus;

import java.util.List;

public interface AppointmentService {
    AppointmentDTO bookAppointment(Long userId, AppointmentRequest request);
    List<AppointmentDTO> getUserAppointments(Long userId);
    List<AppointmentDTO> getDoctorAppointments(Long doctorId);
    AppointmentDTO updateStatus(Long appointmentId, AppointmentStatus status);
    AppointmentDTO getById(Long id);
}
