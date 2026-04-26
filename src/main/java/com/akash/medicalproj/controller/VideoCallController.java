package com.akash.medicalproj.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VideoCallController {

    @Value("${zego.app.id:}")
    private String zegoAppId;

    @Value("${zego.server.secret:}")
    private String zegoServerSecret;

    @GetMapping("/services/video-call")
    public String videoCallPage(
            @RequestParam(required = false) String roomId,
            @RequestParam(required = false) String role,
            Model model) {

        model.addAttribute("roomId", roomId != null ? roomId : "room-" + System.currentTimeMillis());
        model.addAttribute("role",   role != null ? role : "user");
        // Inject credentials — empty string when not configured (never null)
        model.addAttribute("zegoAppId",       zegoAppId != null ? zegoAppId : "");
        model.addAttribute("zegoServerSecret", zegoServerSecret != null ? zegoServerSecret : "");
        model.addAttribute("zegoConfigured",  zegoAppId != null && !zegoAppId.isBlank());
        return "services/video-call";
    }
}
