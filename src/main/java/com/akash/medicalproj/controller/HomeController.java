package com.akash.medicalproj.controller;

import com.akash.medicalproj.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DoctorService doctorService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("doctors", doctorService.getAllAvailableDoctors());
        return "home";
    }

    @GetMapping("/mental-health")
    public String mentalHealthPage() {
        return "mental-health";
    }
}
