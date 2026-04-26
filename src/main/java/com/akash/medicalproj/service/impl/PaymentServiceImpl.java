package com.akash.medicalproj.service.impl;

import com.akash.medicalproj.dto.PaymentRequest;
import com.akash.medicalproj.entity.Appointment;
import com.akash.medicalproj.entity.Appointment.PaymentStatus;
import com.akash.medicalproj.exception.ResourceNotFoundException;
import com.akash.medicalproj.repository.AppointmentRepository;
import com.akash.medicalproj.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final AppointmentRepository appointmentRepository;

    @Value("${stripe.secret.key:}")
    private String stripeSecretKey;

    @Override
    public Map<String, String> createPaymentIntent(PaymentRequest request) {
        Map<String, String> result = new HashMap<>();

        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            result.put("error", "Stripe not configured. Set stripe.secret.key.");
            result.put("clientSecret", "dummy_secret_for_dev");
            return result;
        }

        try {
            com.stripe.Stripe.apiKey = stripeSecretKey;

            long amountInPaise = request.getAmount()
                    .multiply(java.math.BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInPaise)
                    .setCurrency(request.getCurrency() != null ? request.getCurrency() : "inr")
                    .putMetadata("appointmentId", request.getAppointmentId().toString())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            result.put("clientSecret", intent.getClientSecret());
            result.put("paymentIntentId", intent.getId());
        } catch (StripeException e) {
            log.error("Stripe payment intent creation failed", e);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @Override
    public void confirmPayment(Long appointmentId, String paymentIntentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        appointment.setPaymentStatus(PaymentStatus.PAID);
        appointment.setStripePaymentIntentId(paymentIntentId);
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);
    }
}
