package com.example.KLTN.DTOS.Request;

import lombok.*;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VerifyUserDTO {
    private String email;
    private String verificationCode;
}
