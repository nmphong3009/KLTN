package com.example.KLTN.DTOS.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResponseDTO {
    private Long id;
    private String subjectId;
    private String subjectName;
    private Integer credit;
    private Double grade;
    private Double gradeFor;
    private String gradeABC;
    private String semesterName;
}
