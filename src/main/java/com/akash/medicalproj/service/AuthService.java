package com.akash.medicalproj.service;

import com.akash.medicalproj.dto.AuthResponse;
import com.akash.medicalproj.dto.LoginRequest;
import com.akash.medicalproj.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
