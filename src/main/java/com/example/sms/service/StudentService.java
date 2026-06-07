package com.example.sms.service;

import com.example.sms.dto.request.CourseAssignmentRequestDTO;
import com.example.sms.dto.request.StudentRequestDTO;
import com.example.sms.dto.request.StudentUpdateRequestDTO;
import com.example.sms.dto.response.CourseResponseDTO;
import com.example.sms.dto.response.StudentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface StudentService {
    StudentResponseDTO createStudent(StudentRequestDTO request);

    StudentResponseDTO updateStudent(Long id, StudentRequestDTO request);

    void deleteStudent(Long id);

    StudentResponseDTO getStudentById(Long id);

    Page<StudentResponseDTO> getAllStudents(Pageable pageable);

    Page<StudentResponseDTO> searchStudentsByName(String name, Pageable pageable);

    Page<StudentResponseDTO> getStudentsByCourse(Long courseId, Pageable pageable);

    void assignCoursesToStudent(CourseAssignmentRequestDTO request);

    StudentResponseDTO updateStudentProfile(String studentCode, StudentUpdateRequestDTO request);

    Set<CourseResponseDTO> getStudentCourses(String studentCode);

    Set<String> getStudentTopics(String studentCode);

    void leaveACourse(String studentCode, Long courseId);
}
