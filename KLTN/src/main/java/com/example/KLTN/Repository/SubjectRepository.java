package com.example.KLTN.Repository;

import com.example.KLTN.Entity.Major;
import com.example.KLTN.Entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    @Query("SELECT s FROM Subject s WHERE s.subjectId = :subjectId")
    Subject findBySubjectId(@Param("subjectId") String subjectId);
}
