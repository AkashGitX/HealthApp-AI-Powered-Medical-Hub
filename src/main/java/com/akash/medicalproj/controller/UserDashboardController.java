package com.akash.medicalproj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Returns the User Dashboard shell page.
 * Security: page is public — JS on the page reads JWT from localStorage,
 * redirects to /auth/login if absent, and fetches all data via fetch() API calls.
 */
@Controller
@RequestMapping("/dashboard/user")
public class UserDashboardController {

    @GetMapping
    public String dashboard() {
        return "user/dashboard";
    }
}
