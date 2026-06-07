package com.example.sms.service;

import com.example.sms.dto.request.CourseRequestDTO;
import com.example.sms.dto.response.CourseResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {
    CourseResponseDTO createCourse(CourseRequestDTO request);

    CourseResponseDTO updateCourse(Long id, CourseRequestDTO request);

    void deleteCourse(Long id);

    CourseResponseDTO getCourseById(Long id);

    Page<CourseResponseDTO> getAllCourses(Pageable pageable);
}
