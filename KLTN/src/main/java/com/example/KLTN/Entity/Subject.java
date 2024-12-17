package com.example.KLTN.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "subject")
// mon hoc
public class Subject extends AbstractEntity<Long>{
    @Column
    private String subjectId;
    @Column
    private String subjectName;
    @Column
    private Integer credit;

    @OneToMany(mappedBy = "subject")
    private Set<Score> scores;
}

