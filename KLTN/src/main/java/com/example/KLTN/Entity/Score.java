package com.example.KLTN.Entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "score")

public class Score extends AbstractEntity<Long>{
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @Column
    private Double grade;
}
