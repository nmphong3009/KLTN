package com.example.KLTN.DTOS.Request;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MajorRequest {
    private Long id;
    private String majorName;
    private Long facultyId;
}
