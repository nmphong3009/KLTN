package com.example.KLTN.DTOS.Response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MajorResponse {
    private Long id;
    private String majorName;
    private String facultyName;
}
