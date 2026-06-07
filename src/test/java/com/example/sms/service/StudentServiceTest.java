package com.example.sms.service;

import com.example.sms.dto.request.StudentRequestDTO;
import com.example.sms.dto.response.StudentResponseDTO;
import com.example.sms.entity.Gender;
import com.example.sms.entity.Student;
import com.example.sms.exception.DuplicateResourceException;
import com.example.sms.exception.ResourceNotFoundException;
import com.example.sms.mapper.AddressMapper;
import com.example.sms.mapper.CourseMapper;
import com.example.sms.mapper.StudentMapper;
import com.example.sms.repository.CourseRepository;
import com.example.sms.repository.StudentRepository;
import com.example.sms.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private StudentMapper studentMapper;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private CourseMapper courseMapper;
    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private StudentRequestDTO requestDTO;
    private StudentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setStudentCode("STU001");
        student.setName("Test Student");
        student.setDob(LocalDate.of(2000, 1, 1));
        student.setGender(Gender.MALE);

        requestDTO = new StudentRequestDTO();
        requestDTO.setStudentCode("STU001");
        requestDTO.setName("Test Student");
        requestDTO.setDob(LocalDate.of(2000, 1, 1));
        requestDTO.setGender(Gender.MALE);

        responseDTO = new StudentResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setStudentCode("STU001");
        responseDTO.setName("Test Student");
    }

    @Test
    @DisplayName("Create student successfully")
    void createStudent_Success() {
        when(studentRepository.existsByStudentCode("STU001")).thenReturn(false);
        when(studentMapper.toEntity(any())).thenReturn(student);
        when(studentRepository.save(any())).thenReturn(student);
        when(studentMapper.toResponseDTO(any())).thenReturn(responseDTO);

        StudentResponseDTO result = studentService.createStudent(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getStudentCode()).isEqualTo("STU001");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Create student throws DuplicateResourceException")
    void createStudent_ThrowsDuplicate() {
        when(studentRepository.existsByStudentCode("STU001")).thenReturn(true);

        assertThatThrownBy(() -> studentService.createStudent(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("STU001");

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get student by ID successfully")
    void getStudentById_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.toResponseDTO(any())).thenReturn(responseDTO);

        StudentResponseDTO result = studentService.getStudentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Get student by ID throws ResourceNotFoundException")
    void getStudentById_NotFound() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Delete student successfully")
    void deleteStudent_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        doNothing().when(studentRepository).delete(any());

        assertThatCode(() -> studentService.deleteStudent(1L)).doesNotThrowAnyException();
        verify(studentRepository).delete(student);
    }

    @Test
    @DisplayName("Delete student throws ResourceNotFoundException when not found")
    void deleteStudent_NotFound() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.deleteStudent(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(studentRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Search students by name returns paginated result")
    void searchStudentsByName_Success() {
        Page<Student> page = new PageImpl<>(List.of(student));
        when(studentRepository.findByNameContainingIgnoreCase(anyString(), any())).thenReturn(page);
        when(studentMapper.toResponseDTO(any())).thenReturn(responseDTO);

        Page<StudentResponseDTO> result = studentService.searchStudentsByName("Test", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Student");
    }

    @Test
    @DisplayName("Get all students returns paginated result")
    void getAllStudents_ReturnsPaged() {
        List<Student> students = List.of(student);
        Page<Student> page =
                new PageImpl<>(students, PageRequest.of(0, 10), students.size());
        when(studentRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(studentMapper.toResponseDTO(any())).thenReturn(responseDTO);

        Page<StudentResponseDTO> result = studentService.getAllStudents(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }
}
