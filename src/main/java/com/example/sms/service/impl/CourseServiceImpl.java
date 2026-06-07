package com.example.sms.service.impl;

import com.example.sms.dto.request.CourseRequestDTO;
import com.example.sms.dto.response.CourseResponseDTO;
import com.example.sms.entity.Course;
import com.example.sms.exception.DuplicateResourceException;
import com.example.sms.exception.ResourceNotFoundException;
import com.example.sms.mapper.CourseMapper;
import com.example.sms.repository.CourseRepository;
import com.example.sms.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO request) {
        log.info("Creating new course: {}", request.getCourseName());

        if (courseRepository.existsByCourseName(request.getCourseName())) {
            throw new DuplicateResourceException("Course already exists with name: " + request.getCourseName());
        }

        Course course = courseMapper.toEntity(request);
        Course saved = courseRepository.save(course);
        log.info("Course created with id: {}", saved.getId());
        return courseMapper.toResponseDTO(saved);
    }

    @Override
    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO request) {
        log.info("Updating course with id: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));

        courseMapper.updateEntityFromDTO(request, course);
        Course saved = courseRepository.save(course);

        log.info("Course updated: {}", id);
        return courseMapper.toResponseDTO(saved);
    }

    @Override
    public void deleteCourse(Long id) {
        log.info("Deleting course with id: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));

        courseRepository.delete(course);
        log.info("Course deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
        return courseMapper.toResponseDTO(course);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDTO> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable)
                .map(courseMapper::toResponseDTO);
    }
}
