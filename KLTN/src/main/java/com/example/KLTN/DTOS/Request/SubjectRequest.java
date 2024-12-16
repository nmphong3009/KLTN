package com.example.KLTN.DTOS.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SubjectRequest {
    @NotBlank
    private Long id;
    private String subjectId;
    private String subjectName;
    private Integer credit;

}
