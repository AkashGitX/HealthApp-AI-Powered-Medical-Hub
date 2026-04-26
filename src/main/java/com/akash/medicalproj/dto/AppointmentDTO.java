package com.akash.medicalproj.dto;

import com.akash.medicalproj.entity.Appointment.AppointmentStatus;
import com.akash.medicalproj.entity.Appointment.AppointmentType;
import com.akash.medicalproj.entity.Appointment.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class AppointmentDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentType type;
    private AppointmentStatus status;
    private String reason;
    private String notes;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String meetingRoomId;
    private LocalDateTime createdAt;
}
