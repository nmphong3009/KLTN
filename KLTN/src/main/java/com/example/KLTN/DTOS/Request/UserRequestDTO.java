package com.example.KLTN.DTOS.Request;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserRequestDTO {
    private String studentId;
    private String studentName;
    private String phoneNumber;
    private String email;
}

