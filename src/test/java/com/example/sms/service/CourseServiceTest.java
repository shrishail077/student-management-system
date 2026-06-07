package com.example.sms.service;

import com.example.sms.dto.request.CourseRequestDTO;
import com.example.sms.dto.response.CourseResponseDTO;
import com.example.sms.entity.Course;
import com.example.sms.exception.DuplicateResourceException;
import com.example.sms.exception.ResourceNotFoundException;
import com.example.sms.mapper.CourseMapper;
import com.example.sms.repository.CourseRepository;
import com.example.sms.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService Tests")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseMapper courseMapper;
    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course;
    private CourseRequestDTO requestDTO;
    private CourseResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);
        course.setCourseName("Java Basics");
        course.setCourseType("Technical");
        course.setDuration("3 months");

        requestDTO = new CourseRequestDTO();
        requestDTO.setCourseName("Java Basics");
        requestDTO.setCourseType("Technical");
        requestDTO.setDuration("3 months");

        responseDTO = new CourseResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCourseName("Java Basics");
    }

    @Test
    @DisplayName("Create course successfully")
    void createCourse_Success() {
        when(courseRepository.existsByCourseName(anyString())).thenReturn(false);
        when(courseMapper.toEntity(any())).thenReturn(course);
        when(courseRepository.save(any())).thenReturn(course);
        when(courseMapper.toResponseDTO(any())).thenReturn(responseDTO);

        CourseResponseDTO result = courseService.createCourse(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getCourseName()).isEqualTo("Java Basics");
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("Create course throws DuplicateResourceException when name exists")
    void createCourse_ThrowsDuplicate() {
        when(courseRepository.existsByCourseName(anyString())).thenReturn(true);

        assertThatThrownBy(() -> courseService.createCourse(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Java Basics");

        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get course by ID successfully")
    void getCourseById_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseMapper.toResponseDTO(any())).thenReturn(responseDTO);

        CourseResponseDTO result = courseService.getCourseById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Get course by ID throws ResourceNotFoundException")
    void getCourseById_NotFound() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Get all courses returns paginated result")
    void getAllCourses_ReturnsPaged() {
        List<Course> courses = List.of(course);
        Page<Course> page =
                new PageImpl<>(
                        courses,
                        PageRequest.of(0, 10),
                        courses.size()
                );
        when(courseRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(courseMapper.toResponseDTO(any())).thenReturn(responseDTO);

        Page<CourseResponseDTO> result = courseService.getAllCourses(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCourseName()).isEqualTo("Java Basics");
    }

    @Test
    @DisplayName("Delete course successfully")
    void deleteCourse_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        doNothing().when(courseRepository).delete(any());

        assertThatCode(() -> courseService.deleteCourse(1L)).doesNotThrowAnyException();
        verify(courseRepository).delete(course);
    }

    @Test
    @DisplayName("Delete course throws ResourceNotFoundException when not found")
    void deleteCourse_NotFound() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.deleteCourse(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(courseRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Update course successfully")
    void updateCourse_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any())).thenReturn(course);
        when(courseMapper.toResponseDTO(any())).thenReturn(responseDTO);

        CourseResponseDTO result = courseService.updateCourse(1L, requestDTO);

        assertThat(result).isNotNull();
        verify(courseMapper).updateEntityFromDTO(eq(requestDTO), eq(course));
    }
}
