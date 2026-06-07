package com.example.sms.controller;

import com.example.sms.dto.request.LoginRequestDTO;
import com.example.sms.dto.request.StudentLoginRequestDTO;
import com.example.sms.dto.response.ApiResponseDTO;
import com.example.sms.dto.response.LoginResponseDTO;
import com.example.sms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Admin and Student login endpoints")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/admin/login")
    @Operation(summary = "Admin Login", description = "Authenticate admin and get JWT token")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> adminLogin(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.success("Login successful", authService.adminLogin(request)));
    }

    @PostMapping("/student/login")
    @Operation(summary = "Student Login", description = "Authenticate student using studentCode and dateOfBirth")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> studentLogin(@Valid @RequestBody StudentLoginRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.success("Login successful", authService.studentLogin(request)));
    }
}
