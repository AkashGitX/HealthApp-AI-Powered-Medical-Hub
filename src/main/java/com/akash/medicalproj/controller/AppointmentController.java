package com.akash.medicalproj.controller;

import com.akash.medicalproj.dto.ApiResponse;
import com.akash.medicalproj.dto.AppointmentDTO;
import com.akash.medicalproj.dto.AppointmentRequest;
import com.akash.medicalproj.entity.Appointment.AppointmentStatus;
import com.akash.medicalproj.entity.User;
import com.akash.medicalproj.service.AppointmentService;
import com.akash.medicalproj.service.DoctorService;
import com.akash.medicalproj.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final UserService userService;

    @Value("${stripe.public.key:}")
    private String stripePublicKey;

    // ── Page: book appointment (OFFLINE/clinic doctors only) ────
    @GetMapping("/services/book-appointment")
    public String bookAppointmentPage(Model model) {
        model.addAttribute("stripePublicKey", stripePublicKey);
        return "services/book-appointment";
    }

    // ── Page: online consultation (ONLINE/video doctors only) ───
    @GetMapping("/services/online-consultation")
    public String onlineConsultationPage(Model model) {
        model.addAttribute("stripePublicKey", stripePublicKey);
        return "services/online-consultation";
    }

    // ── REST: book appointment ──────────────────────────────────
    @PostMapping("/api/appointments")
    @ResponseBody
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<AppointmentDTO>> book(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AppointmentRequest request) {

        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        AppointmentDTO dto = appointmentService.bookAppointment(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(dto, "Appointment booked successfully"));
    }

    // ── REST: get user appointments ─────────────────────────────
    @GetMapping("/api/appointments/my")
    @ResponseBody
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> myAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.ok(appointmentService.getUserAppointments(user.getId()), "Success"));
    }

    // ── REST: doctor manages appointment ────────────────────────
    @PatchMapping("/api/appointments/{id}/status")
    @ResponseBody
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentDTO>> updateStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {

        return ResponseEntity.ok(
                ApiResponse.ok(appointmentService.updateStatus(id, status), "Status updated"));
    }

    // ── REST: doctor's appointments ─────────────────────────────
    @GetMapping("/api/appointments/doctor")
    @ResponseBody
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> doctorAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.getUserEntityByEmail(userDetails.getUsername());
        var doctor = doctorService.getDoctorByUserId(user.getId());
        return ResponseEntity.ok(
                ApiResponse.ok(appointmentService.getDoctorAppointments(doctor.getId()), "Success"));
    }
}
