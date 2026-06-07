package com.example.sms.controller;

import com.example.sms.dto.request.StudentUpdateRequestDTO;
import com.example.sms.dto.response.ApiResponseDTO;
import com.example.sms.dto.response.CourseResponseDTO;
import com.example.sms.dto.response.StudentResponseDTO;
import com.example.sms.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Student - Self Service")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {
    private final StudentService studentService;

    private String extractStudentCode(Authentication authentication) {
        return authentication.getName().replace("student_", "");
    }

    @PutMapping("/profile")
    @Operation(summary = "Update student profile")
    public ResponseEntity<ApiResponseDTO<StudentResponseDTO>> updateProfile(
            Authentication authentication, @Valid @RequestBody StudentUpdateRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.success("Profile updated successfully",
                studentService.updateStudentProfile(extractStudentCode(authentication), request)));
    }

    @GetMapping("/courses")
    @Operation(summary = "View enrolled courses")
    public ResponseEntity<ApiResponseDTO<Set<CourseResponseDTO>>> getMyCourses(Authentication authentication) {
        return ResponseEntity.ok(ApiResponseDTO.success(studentService.getStudentCourses(extractStudentCode(authentication))));
    }

    @GetMapping("/topics")
    @Operation(summary = "Search topics from assigned courses")
    public ResponseEntity<ApiResponseDTO<Set<String>>> getMyTopics(Authentication authentication) {
        return ResponseEntity.ok(ApiResponseDTO.success(studentService.getStudentTopics(extractStudentCode(authentication))));
    }

    @DeleteMapping("/courses/{courseId}")
    @Operation(summary = "Leave a course")
    public ResponseEntity<ApiResponseDTO<Void>> leaveCourse(Authentication authentication, @PathVariable Long courseId) {
        studentService.leaveACourse(extractStudentCode(authentication), courseId);
        return ResponseEntity.ok(ApiResponseDTO.success("Successfully left the course", null));
    }
}
