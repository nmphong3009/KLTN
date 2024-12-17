package com.example.KLTN.Repository;

import com.example.KLTN.Entity.Score;
import com.example.KLTN.Entity.Subject;
import com.example.KLTN.Entity.User;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score,Long> {

    Optional<Score> findByUserAndSubject(User user, Subject subject);

    List<Score> findByUser(User user);
}