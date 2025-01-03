package com.example.KLTN.DTOS.Request;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LecturerRequest {
    private Long id;
    private String lecturerId;
    private String lecturerName;
    private String lecturerPhone;
    private String lecturerMail;

    private Set<Long> subjectIds;
}
