package com.akash.medicalproj.dto;

import com.akash.medicalproj.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private LocalDateTime createdAt;
}
