package com.example.KLTN.Repository;

import com.example.KLTN.Entity.Major;
import com.example.KLTN.Entity.MajorSubject;
import com.example.KLTN.Entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MajorSubjectRepository extends JpaRepository<MajorSubject,Long> {
    List<MajorSubject> findByMajor(Major major);

    MajorSubject findByMajorAndSubject(Major major, Subject subject);
}
