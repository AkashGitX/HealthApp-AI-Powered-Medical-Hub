package com.akash.medicalproj.service;

import com.akash.medicalproj.dto.PaymentRequest;

import java.util.Map;

public interface PaymentService {
    Map<String, String> createPaymentIntent(PaymentRequest request);
    void confirmPayment(Long appointmentId, String paymentIntentId);
}
