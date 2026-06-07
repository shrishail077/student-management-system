package com.example.sms.controller;

import com.example.sms.dto.request.CourseAssignmentRequestDTO;
import com.example.sms.dto.request.StudentRequestDTO;
import com.example.sms.dto.response.ApiResponseDTO;
import com.example.sms.dto.response.StudentResponseDTO;
import com.example.sms.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Student Management")
@SecurityRequirement(name = "bearerAuth")
public class AdminStudentController {
    private final StudentService studentService;

    @PostMapping("/students")
    @Operation(summary = "Create student")
    public ResponseEntity<ApiResponseDTO<StudentResponseDTO>> createStudent(@Valid @RequestBody StudentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Student created successfully", studentService.createStudent(request)));
    }

    @PutMapping("/students/{id}")
    @Operation(summary = "Update student")
    public ResponseEntity<ApiResponseDTO<StudentResponseDTO>> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.success("Student updated successfully", studentService.updateStudent(id, request)));
    }

    @DeleteMapping("/students/{id}")
    @Operation(summary = "Delete student")
    public ResponseEntity<ApiResponseDTO<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Student deleted successfully", null));
    }

    @GetMapping("/students/{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<ApiResponseDTO<StudentResponseDTO>> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(studentService.getStudentById(id)));
    }

    @GetMapping("/students")
    @Operation(summary = "Get all students with pagination")
    public ResponseEntity<ApiResponseDTO<Page<StudentResponseDTO>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return ResponseEntity.ok(ApiResponseDTO.success(studentService.getAllStudents(PageRequest.of(page, size, sort))));
    }

    @GetMapping("/students/search")
    @Operation(summary = "Search students by name")
    public ResponseEntity<ApiResponseDTO<Page<StudentResponseDTO>>> searchStudents(
            @RequestParam String name, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponseDTO.success(studentService.searchStudentsByName(name, PageRequest.of(page, size))));
    }

    @GetMapping("/courses/{courseId}/students")
    @Operation(summary = "Get students enrolled in a specific course")
    public ResponseEntity<ApiResponseDTO<Page<StudentResponseDTO>>> getStudentsByCourse(
            @PathVariable Long courseId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponseDTO.success(studentService.getStudentsByCourse(courseId, PageRequest.of(page, size))));
    }

    @PostMapping("/course-assignments")
    @Operation(summary = "Assign courses to a student")
    public ResponseEntity<ApiResponseDTO<Void>> assignCourses(@Valid @RequestBody CourseAssignmentRequestDTO request) {
        studentService.assignCoursesToStudent(request);
        return ResponseEntity.ok(ApiResponseDTO.success("Courses assigned successfully", null));
    }
}
