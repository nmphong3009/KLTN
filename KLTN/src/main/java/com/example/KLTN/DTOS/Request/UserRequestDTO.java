package com.example.KLTN.DTOS.Request;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserRequestDTO {
    @NotBlank
    private Long id;
    private String studentId;
    private String studentName;
    private String phoneNumber;
    private String email;
}

