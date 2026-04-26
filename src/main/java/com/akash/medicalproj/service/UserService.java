package com.akash.medicalproj.service;

import com.akash.medicalproj.dto.UserDTO;
import com.akash.medicalproj.entity.User;

public interface UserService {
    UserDTO getUserById(Long id);
    UserDTO getUserByEmail(String email);
    User getUserEntityByEmail(String email);
}
