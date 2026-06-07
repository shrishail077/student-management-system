package com.example.sms.service;

import com.example.sms.dto.request.LoginRequestDTO;
import com.example.sms.dto.request.StudentLoginRequestDTO;
import com.example.sms.dto.response.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO adminLogin(LoginRequestDTO request);

    LoginResponseDTO studentLogin(StudentLoginRequestDTO request);
}
