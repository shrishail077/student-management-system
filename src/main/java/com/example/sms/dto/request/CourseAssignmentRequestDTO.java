package com.example.sms.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CourseAssignmentRequestDTO {
    @NotNull(message = "Student ID is required")
    private Long studentId;
    @NotEmpty(message = "At least one course ID is required")
    private List<Long> courseIds;
}
