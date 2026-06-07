package com.example.sms.controller;

import com.example.sms.config.TestSecurityConfig;
import com.example.sms.dto.request.StudentRequestDTO;
import com.example.sms.dto.response.StudentResponseDTO;
import com.example.sms.entity.Gender;
import com.example.sms.security.JwtUtil;
import com.example.sms.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminStudentController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@DisplayName("AdminStudentController Tests")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    private StudentRequestDTO requestDTO;
    private StudentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new StudentRequestDTO();
        requestDTO.setStudentCode("STU001");
        requestDTO.setName("Rahul Sharma");
        requestDTO.setDob(LocalDate.of(2000, 5, 15));
        requestDTO.setGender(Gender.MALE);

        responseDTO = new StudentResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setStudentCode("STU001");
        responseDTO.setName("Rahul Sharma");
        responseDTO.setGender(Gender.MALE);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/admin/students - Create student successfully")
    void createStudent_ReturnsCreated() throws Exception {
        when(studentService.createStudent(any(StudentRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/admin/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studentCode").value("STU001"))
                .andExpect(jsonPath("$.data.name").value("Rahul Sharma"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/students/{id} - Get student by ID")
    void getStudentById_ReturnsStudent() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/admin/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/students - Get all students paginated")
    void getAllStudents_ReturnsPaginatedList() throws Exception {
        Page<StudentResponseDTO> page =
                new PageImpl<>(
                        List.of(responseDTO),
                        PageRequest.of(0, 10),
                        1
                );
        when(studentService.getAllStudents(any())).thenReturn(page);

        mockMvc.perform(get("/api/admin/students")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/admin/students/{id} - Delete student")
    void deleteStudent_ReturnsSuccess() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/api/admin/students/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/students/search - Search by name")
    void searchStudents_ReturnsMatchingStudents() throws Exception {
        Page<StudentResponseDTO> page =
                new PageImpl<>(
                        List.of(responseDTO),
                        PageRequest.of(0, 10),
                        1
                );
        when(studentService.searchStudentsByName(anyString(), any())).thenReturn(page);

        mockMvc.perform(get("/api/admin/students/search")
                        .param("name", "Rahul"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("POST /api/admin/students - Forbidden for STUDENT role")
    void createStudent_ForbiddenForStudentRole() throws Exception {
        mockMvc.perform(post("/api/admin/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/admin/students - Unauthorized without token")
    void createStudent_UnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(post("/api/admin/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }
}
