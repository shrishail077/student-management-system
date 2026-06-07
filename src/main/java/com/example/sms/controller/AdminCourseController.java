package com.example.sms.controller;

import com.example.sms.dto.request.CourseRequestDTO;
import com.example.sms.dto.response.ApiResponseDTO;
import com.example.sms.dto.response.CourseResponseDTO;
import com.example.sms.service.CourseService;
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
@Tag(name = "Admin - Course Management")
@SecurityRequirement(name = "bearerAuth")
public class AdminCourseController {
    private final CourseService courseService;

    @PostMapping("/courses")
    @Operation(summary = "Create a new course")
    public ResponseEntity<ApiResponseDTO<CourseResponseDTO>> createCourse(@Valid @RequestBody CourseRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Course created successfully", courseService.createCourse(request)));
    }

    @PutMapping("/courses/{id}")
    @Operation(summary = "Update an existing course")
    public ResponseEntity<ApiResponseDTO<CourseResponseDTO>> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.success("Course updated successfully", courseService.updateCourse(id, request)));
    }

    @DeleteMapping("/courses/{id}")
    @Operation(summary = "Delete a course")
    public ResponseEntity<ApiResponseDTO<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Course deleted successfully", null));
    }

    @GetMapping("/courses/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<ApiResponseDTO<CourseResponseDTO>> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(courseService.getCourseById(id)));
    }

    @GetMapping("/courses")
    @Operation(summary = "Get all courses with pagination")
    public ResponseEntity<ApiResponseDTO<Page<CourseResponseDTO>>> getAllCourses(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return ResponseEntity.ok(ApiResponseDTO.success(courseService.getAllCourses(PageRequest.of(page, size, sort))));
    }
}
