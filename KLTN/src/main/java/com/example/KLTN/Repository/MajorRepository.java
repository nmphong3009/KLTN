package com.example.KLTN.Repository;

import com.example.KLTN.Entity.Faculty;
import com.example.KLTN.Entity.Major;
import com.example.KLTN.Entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major,Long> {
    List<Major> findByFaculty(Faculty faculty);

    @Query("SELECT m FROM Major m WHERE m.majorName = :majorName")
    Optional<Major> findByMajorName(@Param("majorName") String majorName);

}
