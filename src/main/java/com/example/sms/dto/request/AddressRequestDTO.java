package com.example.sms.dto.request;

import com.example.sms.entity.AddressType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddressRequestDTO {
    @NotNull(message = "Address type is required")
    private AddressType type;
    @NotBlank(message = "Street is required")
    private String street;
    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "State is required")
    private String state;
    @NotBlank(message = "Country is required")
    private String country;
    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    private String pincode;
}
