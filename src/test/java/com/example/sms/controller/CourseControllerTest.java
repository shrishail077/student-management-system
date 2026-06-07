package com.example.sms.controller;

import com.example.sms.config.TestSecurityConfig;
import com.example.sms.dto.request.CourseRequestDTO;
import com.example.sms.dto.response.CourseResponseDTO;
import com.example.sms.security.JwtUtil;
import com.example.sms.service.CourseService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCourseController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@DisplayName("AdminCourseController Tests")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    private CourseRequestDTO requestDTO;
    private CourseResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new CourseRequestDTO();
        requestDTO.setCourseName("Java Spring Boot");
        requestDTO.setDescription("Enterprise Java with Spring Boot");
        requestDTO.setCourseType("Technical");
        requestDTO.setDuration("3 months");
        requestDTO.setTopics("Spring Boot, JPA, Security");

        responseDTO = new CourseResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCourseName("Java Spring Boot");
        responseDTO.setCourseType("Technical");
        responseDTO.setDuration("3 months");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/admin/courses - Create course successfully")
    void createCourse_ReturnsCreated() throws Exception {
        when(courseService.createCourse(any(CourseRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/admin/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.courseName").value("Java Spring Boot"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/courses/{id} - Get course by ID")
    void getCourseById_ReturnsCourse() throws Exception {
        when(courseService.getCourseById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/admin/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/courses - Get all courses paginated")
    void getAllCourses_ReturnsList() throws Exception {
        Page<CourseResponseDTO> page =
                new PageImpl<>(
                        List.of(responseDTO),
                        PageRequest.of(0, 10),
                        1
                );
        when(courseService.getAllCourses(any())).thenReturn(page);

        mockMvc.perform(get("/api/admin/courses")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/admin/courses/{id} - Update course")
    void updateCourse_ReturnsUpdated() throws Exception {
        when(courseService.updateCourse(eq(1L), any(CourseRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/admin/courses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/admin/courses/{id} - Delete course")
    void deleteCourse_ReturnsSuccess() throws Exception {
        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/api/admin/courses/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/admin/courses - Unauthorized without token")
    void createCourse_UnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(post("/api/admin/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("POST /api/admin/courses - Forbidden for STUDENT role")
    void createCourse_ForbiddenForStudent() throws Exception {
        mockMvc.perform(post("/api/admin/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }
}
