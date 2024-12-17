package com.example.KLTN.DTOS.Response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GpaDTO {
    private BigDecimal gpa;
    private Integer totalCredits;
}
