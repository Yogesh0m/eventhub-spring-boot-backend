package com.flowtech.eventhub_spring_boot_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowtech.eventhub_spring_boot_backend.dto.UserLoginRequestDTO;
import com.flowtech.eventhub_spring_boot_backend.dto.UserLoginResponseDTO;
import com.flowtech.eventhub_spring_boot_backend.dto.UserRegisterRequestDTO;
import com.flowtech.eventhub_spring_boot_backend.entity.User;
import com.flowtech.eventhub_spring_boot_backend.security.EventJwtCodeGenerator;
import com.flowtech.eventhub_spring_boot_backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final EventJwtCodeGenerator codeGenerator;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequestDTO requestDTO) {
        return userService.registerUser(requestDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginController(@RequestBody UserLoginRequestDTO dto) {
        User user = userService.findUserByEmail(dto.getEmail());

        if (user.getPassword() == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Password is not set or incorrect");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        UserLoginResponseDTO responseDTO = new UserLoginResponseDTO();
        responseDTO.setEmail(dto.getEmail());
        responseDTO.setRoleName(user.getRole().getName());
        responseDTO.setToken(codeGenerator.generateJwtCode(authentication));

        return ResponseEntity.ok(responseDTO);
    }
}
