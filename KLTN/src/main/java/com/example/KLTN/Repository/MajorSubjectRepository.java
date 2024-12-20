package com.example.KLTN.Repository;

import com.example.KLTN.Entity.MajorSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorSubjectRepository extends JpaRepository<MajorSubject,Long> {
}
