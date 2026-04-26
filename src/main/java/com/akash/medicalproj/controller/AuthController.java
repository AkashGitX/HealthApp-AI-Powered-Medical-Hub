package com.akash.medicalproj.controller;

import com.akash.medicalproj.dto.AuthResponse;
import com.akash.medicalproj.dto.LoginRequest;
import com.akash.medicalproj.dto.RegisterRequest;
import com.akash.medicalproj.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── Page routes ──────────────────────────────────────────────
    @GetMapping("/auth/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    // ── REST API ──────────────────────────────────────────────────
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
