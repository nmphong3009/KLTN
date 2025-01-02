package com.example.KLTN.DTOS.Request;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPassRequest {
    private String email;
    private String studentId;
}
