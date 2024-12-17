package com.example.KLTN.Service;

import com.example.KLTN.DTOS.Response.GpaDTO;
import com.example.KLTN.DTOS.Response.ScoreResponseDTO;
import com.example.KLTN.Entity.Score;
import com.example.KLTN.Entity.Subject;
import com.example.KLTN.Entity.User;
import com.example.KLTN.Repository.ScoreRepository;
import com.example.KLTN.Repository.SubjectRepository;
import com.example.KLTN.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class ScoreService {
    public final UserService userService;
    public final SubjectService subjectService;
    public final SubjectRepository subjectRepository;
    public final UserRepository userRepository;
    public final ScoreRepository scoreRepository;


    public ResponseEntity<?> addScore(Long subjectId, Double grade) {
        User user = userService.getCurrentUser();
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        if (scoreRepository.findByUserAndSubject(user, subject).isPresent()) {
            throw new RuntimeException("User has already enrolled in this subject");
        }
        Score score = Score.builder()
                .user(user)
                .subject(subject)
                .grade(grade)
                .build();
        scoreRepository.save(score);
        return ResponseEntity.ok("Create score successful?");
    }

    public ResponseEntity<?> updateScore(Long subjectId, Double grade) {
        User user = userService.getCurrentUser();
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        Score score = scoreRepository.findByUserAndSubject(user, subject)
                .orElseThrow(() -> new RuntimeException(""));
        score.setGrade(grade);
        scoreRepository.save(score);
        return ResponseEntity.ok("Update score successful?");
    }

    public List<ScoreResponseDTO> getScore() {
        User user = userService.getCurrentUser();
        List<Score> scoreList = scoreRepository.findByUser(user);

        return scoreList.stream().map(score -> {
            Double grade = score.getGrade();
            Double gradeFor = convertTo4Scale(grade); // Chuyển từ hệ 10 sang hệ 4
            String gradeABC = convertToABC(grade); // Chuyển từ hệ 10 sang hệ ABC

            return ScoreResponseDTO.builder()
                    .subjectName(score.getSubject().getSubjectName())
                    .subjectId(score.getSubject().getSubjectId())
                    .credit(score.getSubject().getCredit())
                    .grade(grade)
                    .gradeFor(gradeFor)
                    .gradeABC(gradeABC)
                    .build();
        }).collect(Collectors.toList());
    }

    public ResponseEntity<GpaDTO> calculateGPA(){
        User user = userService.getCurrentUser();
        List<Score> scoreList = scoreRepository.findByUser(user);
        double totalPoints = 0;
        int totalCredits = 0;
        for (Score score : scoreList) {
            Double gradeFor = convertTo4Scale(score.getGrade());
            Integer credit = score.getSubject().getCredit();

            totalPoints += gradeFor * credit;
            totalCredits += credit;
        }
        double gpa = totalPoints / totalCredits;
        BigDecimal roundedGPA = new BigDecimal(gpa).setScale(2, RoundingMode.HALF_UP);
        return new ResponseEntity<>(GpaDTO.builder()
                .gpa(roundedGPA)
                .totalCredits(totalCredits)
                .build(), HttpStatus.OK);
    }

    private String convertToABC(Double grade) {
        if (grade >= 9.0) {
            return "A+";
        } else if (grade <= 8.9 && grade >= 8.5) {
            return "A";
        } else if (grade <= 8.4 && grade >= 8.0) {
            return "B+";
        } else if (grade <= 7.9 && grade >= 7.0) {
            return "B";
        } else if (grade <= 6.9 && grade >= 6.5) {
            return "C+";
        } else if (grade <= 6.4 && grade >= 5.5) {
            return "C";
        } else if (grade <= 5.4 && grade >= 5.0) {
            return "D+";
        } else if (grade <= 4.9 && grade >= 4.0) {
            return "D";
        } else {
            return "F";
        }
    }

    private Double convertTo4Scale(Double grade) {
        if (grade >= 9.0) {
            return 4.0;
        } else if (grade <= 8.9 && grade >= 8.5) {
            return 3.7;
        } else if (grade <= 8.4 && grade >= 8.0) {
            return 3.5;
        } else if (grade <= 7.9 && grade >= 7.0) {
            return 3.0;
        } else if (grade <= 6.9 && grade >= 6.5) {
            return 2.5;
        } else if (grade <= 6.4 && grade >= 5.5) {
            return 2.0;
        } else if (grade <= 5.4 && grade >= 5.0) {
            return 1.5;
        } else if (grade <= 4.9 && grade >= 4.0) {
            return 1.0;
        } else {
            return 0.0;
        }
    }
}
