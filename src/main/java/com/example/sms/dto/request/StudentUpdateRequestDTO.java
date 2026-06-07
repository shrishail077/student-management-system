package com.example.sms.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class StudentUpdateRequestDTO {
    @Email(message = "Invalid email format")
    private String email;
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobile;
    private String fatherName;
    private String motherName;
    @Valid
    private List<AddressRequestDTO> addresses;
}
