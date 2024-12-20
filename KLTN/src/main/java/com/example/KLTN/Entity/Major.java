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
@Table(name = "major")
public class Major extends AbstractEntity<Long>{

    private String majorName;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @OneToMany(mappedBy = "major")
    private Set<User> users;

    @OneToMany(mappedBy = "major")
    private Set<MajorSubject> majorSubjects;
}
