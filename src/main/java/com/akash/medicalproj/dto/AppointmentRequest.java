package com.akash.medicalproj.dto;

import com.akash.medicalproj.entity.Appointment.AppointmentType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    @NotNull private Long doctorId;
    @NotNull private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    @NotNull private AppointmentType type;
    private String reason;
}
