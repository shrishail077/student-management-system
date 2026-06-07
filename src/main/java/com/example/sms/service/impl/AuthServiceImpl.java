package com.example.sms.service.impl;

import com.example.sms.dto.request.LoginRequestDTO;
import com.example.sms.dto.request.StudentLoginRequestDTO;
import com.example.sms.dto.response.LoginResponseDTO;
import com.example.sms.entity.Student;
import com.example.sms.entity.User;
import com.example.sms.exception.ResourceNotFoundException;
import com.example.sms.exception.UnauthorizedException;
import com.example.sms.repository.StudentRepository;
import com.example.sms.repository.UserRepository;
import com.example.sms.security.JwtUtil;
import com.example.sms.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO adminLogin(LoginRequestDTO request) {
        log.info("Admin login attempt for username: {}", request.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", request.getUsername()));

            if (!user.getRole().equals("ROLE_ADMIN")) {
                throw new UnauthorizedException("Access denied: Admin role required");
            }

            String token = jwtUtil.generateToken(userDetails);
            log.info("Admin login successful for: {}", request.getUsername());

            return LoginResponseDTO.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationTime())
                    .role(user.getRole())
                    .username(user.getUsername())
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for username: {}", request.getUsername());
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    @Override
    public LoginResponseDTO studentLogin(StudentLoginRequestDTO request) {
        log.info("Student login attempt for code: {}", request.getStudentCode());

        Student student = studentRepository
                .findByStudentCodeAndDob(request.getStudentCode(), request.getDateOfBirth())
                .orElseThrow(() -> new UnauthorizedException("Invalid student code or date of birth"));

        // create a user account for the student on first login if it doesn't exist yet
        String username = "student_" + student.getStudentCode();
        User studentUser = userRepository.findByUsername(username).orElseGet(() -> {
            User newUser = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(student.getStudentCode() + student.getDob()))
                    .role("ROLE_STUDENT")
                    .build();
            return userRepository.save(newUser);
        });

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(studentUser.getUsername())
                .password(studentUser.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(studentUser.getRole())))
                .build();

        String token = jwtUtil.generateToken(userDetails);
        log.info("Student login successful for: {}", request.getStudentCode());

        return LoginResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationTime())
                .role(studentUser.getRole())
                .username(student.getName())
                .build();
    }
}
