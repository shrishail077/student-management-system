package com.example.sms.dto.response;

import com.example.sms.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {
    private Long id;
    private String studentCode;
    private String name;
    private LocalDate dob;
    private Gender gender;
    private String email;
    private String mobile;
    private String fatherName;
    private String motherName;
    private List<AddressResponseDTO> addresses;
    private Set<CourseResponseDTO> courses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
