package com.example.KLTN.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "semester")
public class Semester extends AbstractEntity<Long> {
    @Column
    private String semesterName;

    @OneToMany(mappedBy = "semester")
    private Set<Score> scores;
}
