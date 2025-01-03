package com.example.KLTN.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "faculty")
public class Faculty extends AbstractEntity<Long> {
    private String facultyName;

    @OneToMany(mappedBy = "faculty")
    private Set<Major> majors;
}
