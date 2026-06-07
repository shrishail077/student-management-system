package com.example.sms.service.impl;

import com.example.sms.dto.request.CourseAssignmentRequestDTO;
import com.example.sms.dto.request.StudentRequestDTO;
import com.example.sms.dto.request.StudentUpdateRequestDTO;
import com.example.sms.dto.response.CourseResponseDTO;
import com.example.sms.dto.response.StudentResponseDTO;
import com.example.sms.entity.Address;
import com.example.sms.entity.Course;
import com.example.sms.entity.Student;
import com.example.sms.exception.BadRequestException;
import com.example.sms.exception.DuplicateResourceException;
import com.example.sms.exception.ResourceNotFoundException;
import com.example.sms.mapper.AddressMapper;
import com.example.sms.mapper.CourseMapper;
import com.example.sms.mapper.StudentMapper;
import com.example.sms.repository.CourseRepository;
import com.example.sms.repository.StudentRepository;
import com.example.sms.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentMapper studentMapper;
    private final AddressMapper addressMapper;
    private final CourseMapper courseMapper;

    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO request) {
        if (studentRepository.existsByStudentCode(request.getStudentCode()))
            throw new DuplicateResourceException("Student already exists with code: " + request.getStudentCode());
        if (request.getEmail() != null && studentRepository.existsByEmail(request.getEmail()))
            throw new DuplicateResourceException("Student already exists with email: " + request.getEmail());
        Student student = studentMapper.toEntity(request);
        if (request.getAddresses() != null) {
            request.getAddresses().forEach(dto -> {
                Address a = addressMapper.toEntity(dto);
                student.addAddress(a);
            });
        }
        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO request) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student", id));
        if (!student.getStudentCode().equals(request.getStudentCode()) && studentRepository.existsByStudentCode(request.getStudentCode()))
            throw new DuplicateResourceException("Student code already in use: " + request.getStudentCode());
        studentMapper.updateEntityFromDTO(request, student);
        if (request.getAddresses() != null) {
            student.getAddresses().clear();
            request.getAddresses().forEach(dto -> {
                Address a = addressMapper.toEntity(dto);
                student.addAddress(a);
            });
        }
        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student", id));
        studentRepository.delete(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        return studentMapper.toResponseDTO(studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable).map(studentMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> searchStudentsByName(String name, Pageable pageable) {
        return studentRepository.findByNameContainingIgnoreCase(name, pageable).map(studentMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getStudentsByCourse(Long courseId, Pageable pageable) {
        if (!courseRepository.existsById(courseId)) throw new ResourceNotFoundException("Course", courseId);
        return studentRepository.findByCourseId(courseId, pageable).map(studentMapper::toResponseDTO);
    }

    @Override
    public void assignCoursesToStudent(CourseAssignmentRequestDTO request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.getStudentId()));
        List<Course> courses = courseRepository.findAllById(request.getCourseIds());
        if (courses.size() != request.getCourseIds().size())
            throw new ResourceNotFoundException("One or more courses not found");
        courses.forEach(student::addCourse);
        studentRepository.save(student);
    }

    @Override
    public StudentResponseDTO updateStudentProfile(String studentCode, StudentUpdateRequestDTO request) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "studentCode", studentCode));
        if (request.getEmail() != null) student.setEmail(request.getEmail());
        if (request.getMobile() != null) student.setMobile(request.getMobile());
        if (request.getFatherName() != null) student.setFatherName(request.getFatherName());
        if (request.getMotherName() != null) student.setMotherName(request.getMotherName());
        if (request.getAddresses() != null && !request.getAddresses().isEmpty()) {
            student.getAddresses().clear();
            request.getAddresses().forEach(dto -> {
                Address a = addressMapper.toEntity(dto);
                student.addAddress(a);
            });
        }
        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<CourseResponseDTO> getStudentCourses(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "studentCode", studentCode));
        return courseMapper.toResponseDTOSet(student.getCourses());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getStudentTopics(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "studentCode", studentCode));
        return student.getCourses().stream()
                .filter(c -> c.getTopics() != null && !c.getTopics().isBlank())
                .flatMap(c -> Arrays.stream(c.getTopics().split(",")))
                .map(String::trim).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public void leaveACourse(String studentCode, Long courseId) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "studentCode", studentCode));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        if (!student.getCourses().contains(course))
            throw new BadRequestException("Student is not enrolled in course: " + courseId);
        student.removeCourse(course);
        studentRepository.save(student);
    }
}
