package com.akash.medicalproj.controller;

import com.akash.medicalproj.dto.PaymentRequest;
import com.akash.medicalproj.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${stripe.public.key:}")
    private String stripePublicKey;

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getConfig() {
        return ResponseEntity.ok(Map.of("publishableKey", stripePublicKey != null ? stripePublicKey : ""));
    }

    @PostMapping("/create-intent")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> createPaymentIntent(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPaymentIntent(request));
    }

    @PostMapping("/confirm")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> confirmPayment(
            @RequestParam Long appointmentId,
            @RequestParam String paymentIntentId) {
        paymentService.confirmPayment(appointmentId, paymentIntentId);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Payment confirmed"));
    }
}
