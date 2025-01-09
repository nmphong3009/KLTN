package com.example.KLTN.DTOS.Request;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LLMRequest {
    private String query;
}
