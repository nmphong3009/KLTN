package com.example.KLTN.DTOS.Request;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor

public class RegisterRequest {
    private String studentId;
    private String password;
    private String confirmPassword;
    private String email;
}

