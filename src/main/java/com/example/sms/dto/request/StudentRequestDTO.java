package com.example.sms.dto.request;

import com.example.sms.entity.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class StudentRequestDTO {
    @NotBlank(message = "Student code is required")
    @Size(min = 3, max = 20, message = "Student code must be between 3 and 20 characters")
    private String studentCode;
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;
    @NotNull(message = "Gender is required")
    private Gender gender;
    @Email(message = "Invalid email format")
    private String email;
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobile;
    private String fatherName;
    private String motherName;
    @Valid
    private List<AddressRequestDTO> addresses;
}
