package com.flowtech.eventhub_spring_boot_backend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flowtech.eventhub_spring_boot_backend.dto.UserRegisterRequestDTO;
import com.flowtech.eventhub_spring_boot_backend.entity.Role;
import com.flowtech.eventhub_spring_boot_backend.entity.User;
import com.flowtech.eventhub_spring_boot_backend.mapper.UserMapper;
import com.flowtech.eventhub_spring_boot_backend.repository.RoleRepository;
import com.flowtech.eventhub_spring_boot_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> registerUser(UserRegisterRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        if (userRepository.existsByPhone(requestDTO.getPhone())) {
            return ResponseEntity.badRequest().body("Phone number already exists");
        }

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new IllegalStateException("Default customer role not found"));

        User user = userMapper.toUser(requestDTO);
        user.setRole(customerRole);
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}
