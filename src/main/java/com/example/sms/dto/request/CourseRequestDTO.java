package com.example.sms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CourseRequestDTO {
    @NotBlank(message = "Course name is required")
    @Size(min = 2, max = 200, message = "Course name must be between 2 and 200 characters")
    private String courseName;
    private String description;
    @NotBlank(message = "Course type is required")
    private String courseType;
    @NotBlank(message = "Duration is required")
    private String duration;
    private String topics;
}
