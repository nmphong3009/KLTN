package com.example.KLTN.Repository;

import com.example.KLTN.Entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer,Long> {
    Lecturer findByLecturerId (String lecturerId);
    @Query("SELECT l FROM Lecturer l JOIN l.subjects s WHERE s.id = :subjectId")
    List<Lecturer> findBySubjectId(@Param("subjectId")  Long subjectId);
    @Query("SELECT l FROM Lecturer l WHERE l.lecturerName = :lecturerName")
    Lecturer findByLecturerName(@Param("lecturerName") String lecturerName);

}
