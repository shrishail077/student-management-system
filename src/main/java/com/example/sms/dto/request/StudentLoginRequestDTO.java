package com.example.sms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentLoginRequestDTO {
    @NotBlank(message = "Student code is required")
    private String studentCode;
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;
}
