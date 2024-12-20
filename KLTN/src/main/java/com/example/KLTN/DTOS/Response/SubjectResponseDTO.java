package com.example.KLTN.DTOS.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponseDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("subjectId")
    private String subjectId;
    @JsonProperty("subjectName")
    private String subjectName;
    @JsonProperty("credit")
    private Integer credit;

    private List<String> majorName;
}
