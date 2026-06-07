package com.example.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDTO {
    private Long id;
    private String courseName;
    private String description;
    private String courseType;
    private String duration;
    private String topics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
