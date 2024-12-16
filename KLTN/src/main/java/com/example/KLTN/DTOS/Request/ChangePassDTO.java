package com.example.KLTN.DTOS.Request;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePassDTO {
    private String oldPass;
    private String password;
    private String confirmPassword;
}

