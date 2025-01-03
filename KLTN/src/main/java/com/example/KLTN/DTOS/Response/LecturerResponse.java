package com.example.KLTN.DTOS.Response;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class LecturerResponse {
    private Long id;
    private String lecturerId;
    private String lecturerName;
    private String lecturerPhone;
    private String lecturerMail;
    private Double averageScore;
}
