package com.example.KLTN.DTOS.Response;

import com.example.KLTN.Enum.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    @JsonProperty("studentId")
    private String studentId;
    @JsonProperty("studentName")
    private String studentName;
    @JsonProperty("role")
    private Role role;
    @JsonProperty("registerDay")
    private LocalDateTime registerDay;
    @JsonProperty("id")
    private Long id;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("email")
    private String email;
    @JsonProperty("majorName")
    private String majorName;
    @JsonProperty("majorName")
    private String facultyName;
}
