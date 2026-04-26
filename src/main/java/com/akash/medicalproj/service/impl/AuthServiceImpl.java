package com.akash.medicalproj.service.impl;

import com.akash.medicalproj.dto.AuthResponse;
import com.akash.medicalproj.dto.AuthResponse;
import com.akash.medicalproj.dto.LoginRequest;
import com.akash.medicalproj.dto.RegisterRequest;
import com.akash.medicalproj.entity.User;
import com.akash.medicalproj.exception.UserAlreadyExistsException;
import com.akash.medicalproj.repository.UserRepository;
import com.akash.medicalproj.security.JwtUtil;
import com.akash.medicalproj.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String redirectUrl = getRedirectUrl(user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .redirectUrl(redirectUrl)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String redirectUrl = getRedirectUrl(user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .redirectUrl(redirectUrl)
                .build();
    }

    private String getRedirectUrl(String role) {
        return switch (role) {
            case "DOCTOR"           -> "/dashboard/doctor";
            case "SERVICE_PROVIDER" -> "/dashboard/provider";
            default                 -> "/dashboard/user";
        };
    }
}
