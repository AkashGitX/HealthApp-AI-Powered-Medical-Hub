package com.akash.medicalproj.service.impl;

import com.akash.medicalproj.dto.AppointmentDTO;
import com.akash.medicalproj.dto.AppointmentRequest;
import com.akash.medicalproj.entity.Appointment;
import com.akash.medicalproj.entity.Appointment.AppointmentStatus;
import com.akash.medicalproj.entity.Appointment.AppointmentType;
import com.akash.medicalproj.entity.Doctor;
import com.akash.medicalproj.entity.User;
import com.akash.medicalproj.exception.ResourceNotFoundException;
import com.akash.medicalproj.repository.AppointmentRepository;
import com.akash.medicalproj.repository.DoctorRepository;
import com.akash.medicalproj.repository.UserRepository;
import com.akash.medicalproj.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public AppointmentDTO bookAppointment(Long userId, AppointmentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", request.getDoctorId()));

        // Enforce doctor type: ONLINE doctors → ONLINE appointment; OFFLINE doctors → IN_PERSON
        AppointmentType enforcedType;
        if (doctor.getDoctorType() == null || doctor.getDoctorType() == Doctor.DoctorType.ONLINE) {
            enforcedType = AppointmentType.ONLINE;
        } else {
            enforcedType = AppointmentType.IN_PERSON;
        }

        String roomId = null;
        if (AppointmentType.ONLINE.equals(enforcedType)) {
            roomId = "room-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        }

        Appointment appointment = Appointment.builder()
                .user(user)
                .doctor(doctor)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .type(enforcedType)
                .reason(request.getReason())
                .amount(doctor.getConsultationFee())
                .meetingRoomId(roomId)
                .build();

        return toDTO(appointmentRepository.save(appointment));
    }

    @Override
    public List<AppointmentDTO> getUserAppointments(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return appointmentRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public List<AppointmentDTO> getDoctorAppointments(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", doctorId));
        return appointmentRepository.findByDoctorOrderByCreatedAtDesc(doctor)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public AppointmentDTO updateStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        appointment.setStatus(status);
        return toDTO(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentDTO getById(Long id) {
        return toDTO(appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id)));
    }

    private AppointmentDTO toDTO(Appointment a) {
        return AppointmentDTO.builder()
                .id(a.getId())
                .userId(a.getUser().getId())
                .userName(a.getUser().getFullName())
                .doctorId(a.getDoctor().getId())
                .doctorName(a.getDoctor().getUser().getFullName())
                .doctorSpecialization(a.getDoctor().getSpecialization())
                .appointmentDate(a.getAppointmentDate())
                .appointmentTime(a.getAppointmentTime())
                .type(a.getType())
                .status(a.getStatus())
                .reason(a.getReason())
                .notes(a.getNotes())
                .amount(a.getAmount())
                .paymentStatus(a.getPaymentStatus())
                .meetingRoomId(a.getMeetingRoomId())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
