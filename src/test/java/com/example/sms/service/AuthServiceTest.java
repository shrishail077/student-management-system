package com.example.sms.service;

import com.example.sms.dto.request.LoginRequestDTO;
import com.example.sms.dto.response.LoginResponseDTO;
import com.example.sms.entity.User;
import com.example.sms.exception.UnauthorizedException;
import com.example.sms.repository.StudentRepository;
import com.example.sms.repository.UserRepository;
import com.example.sms.security.JwtUtil;
import com.example.sms.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Admin login successful")
    void adminLogin_Success() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("Admin@123");

        User adminUser = User.builder()
                .id(1L).username("admin").password("encoded").role("ROLE_ADMIN")
                .build();

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("admin").password("encoded")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("test-token");
        when(jwtUtil.getExpirationTime()).thenReturn(86400000L);

        LoginResponseDTO result = authService.adminLogin(request);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("test-token");
        assertThat(result.getRole()).isEqualTo("ROLE_ADMIN");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Admin login fails with bad credentials")
    void adminLogin_BadCredentials() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.adminLogin(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid username or password");

        verify(jwtUtil, never()).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Admin login fails when user is not ROLE_ADMIN")
    void adminLogin_FailsForNonAdminUser() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("student_user");
        request.setPassword("pass");

        User nonAdminUser = User.builder()
                .id(2L).username("student_user").password("encoded").role("ROLE_STUDENT")
                .build();

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("student_user").password("encoded")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")))
                .build();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByUsername("student_user")).thenReturn(Optional.of(nonAdminUser));

        assertThatThrownBy(() -> authService.adminLogin(request))
                .isInstanceOf(UnauthorizedException.class);
    }
}
