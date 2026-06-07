package com.example.sms.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String role;
    private String username;
}
