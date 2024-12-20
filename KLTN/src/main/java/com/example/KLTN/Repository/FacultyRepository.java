package com.example.KLTN.Repository;

import com.example.KLTN.Entity.Faculty;
import com.example.KLTN.Entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty,Long> {

    @Query("SELECT f FROM Faculty f WHERE f.facultyName = :facultyName")
    Optional<Faculty> findByFacultyName(@Param("facultyName") String facultyName);
}
