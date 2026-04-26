package com.akash.medicalproj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Returns the Doctor Dashboard shell page.
 * Security: page is public — JS reads JWT from localStorage,
 * redirects to /auth/login if absent, fetches all data via API calls.
 */
@Controller
@RequestMapping("/dashboard/doctor")
public class DoctorDashboardController {

    @GetMapping
    public String dashboard() {
        return "doctor/dashboard";
    }
}
