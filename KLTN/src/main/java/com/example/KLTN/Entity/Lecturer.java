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
@Table(name = "lecturer")
public class Lecturer extends AbstractEntity<Long> {
    private String lecturerId;
    private String lecturerName;
    private String lecturerPhone;
    private String lecturerMail;

    @ManyToMany
    @JoinTable(
            name = "lecturer_subject",
            joinColumns = @JoinColumn(name = "lecturer_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects;

    @OneToMany(mappedBy = "lecturer")
    private Set<Score> scores;
}
