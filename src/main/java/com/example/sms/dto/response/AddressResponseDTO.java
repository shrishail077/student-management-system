package com.example.sms.dto.response;

import com.example.sms.entity.AddressType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {
    private Long id;
    private AddressType type;
    private String street;
    private String city;
    private String state;
    private String country;
    private String pincode;
}
