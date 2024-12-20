package com.example.KLTN.DTOS.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SubjectRequest {
    private Long id;
    private String subjectId;
    private String subjectName;
    private Integer credit;
    private List<Long> majorIds;
}
