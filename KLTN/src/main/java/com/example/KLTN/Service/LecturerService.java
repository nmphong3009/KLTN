package com.example.KLTN.Service;

import com.example.KLTN.DTOS.Request.LecturerRequest;
import com.example.KLTN.DTOS.Response.LecturerResponse;
import com.example.KLTN.DTOS.Response.SubjectResponseDTO;
import com.example.KLTN.DTOS.Response.UserResponseDTO;
import com.example.KLTN.Entity.Lecturer;
import com.example.KLTN.Entity.Score;
import com.example.KLTN.Entity.Subject;
import com.example.KLTN.Repository.LecturerRepository;
import com.example.KLTN.Repository.ScoreRepository;
import com.example.KLTN.Repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LecturerService {
    private final UserService userService;
    private final LecturerRepository lecturerRepository;
    private final SubjectRepository subjectRepository;
    private final ScoreRepository scoreRepository;

    public ResponseEntity<?> create (LecturerRequest request){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Lecturer lecturerExit = lecturerRepository.findByLecturerId(request.getLecturerId());
        if (lecturerExit != null){
            throw new RuntimeException("Giang vien da ton tai");
        }
        Set<Subject> subjects = new HashSet<>();
        for (Long subjectId : request.getSubjectIds()) {
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            subjects.add(subject);
        }
        Lecturer lecturer = Lecturer.builder()
                .lecturerId(request.getLecturerId())
                .lecturerName(request.getLecturerName())
                .lecturerMail(request.getLecturerMail())
                .lecturerPhone(request.getLecturerPhone())
                .subjects(subjects)
                .build();
        lecturerRepository.save(lecturer);
        return ResponseEntity.ok("Tao thanh cong");
    }

    public ResponseEntity<?> delete(Long lecturerId){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Lecturer lecturerExit = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));
        for (Subject subject : lecturerExit.getSubjects()){
            subject.getLecturers().remove(lecturerExit);
        }
        lecturerRepository.delete(lecturerExit);
        return ResponseEntity.ok("Delete successful");
    }

    public List<LecturerResponse> getAllLecturer(){
        List<Lecturer> lecturers = lecturerRepository.findAll();
        return lecturers.stream().map(
                lecturer -> LecturerResponse.builder()
                        .id(lecturer.getId())
                        .lecturerId(lecturer.getLecturerId())
                        .lecturerName(lecturer.getLecturerName())
                        .lecturerMail(lecturer.getLecturerMail())
                        .lecturerPhone(lecturer.getLecturerPhone())
                        .build()
        ).toList();
    }

    public List<SubjectResponseDTO> getAllSubject(Long id){
        List<Subject> subjects = subjectRepository.findByLecturerId(id);
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));
        return subjects.stream().map(
                subject -> {
                    List<Score> scores = scoreRepository.findByLecturerAndSubject(lecturer,subject);
                    double averageScore = scores.stream()
                            .mapToDouble(Score::getGrade) // Lấy giá trị điểm
                            .average() // Tính trung bình
                            .orElse(0.0);
                    return SubjectResponseDTO.builder()
                            .id(subject.getId())
                            .subjectId(subject.getSubjectId())
                            .subjectName(subject.getSubjectName())
                            .credit(subject.getCredit())
                            .averageScore(averageScore)
                            .build();
                }
        ).toList();
    }

    public ResponseEntity<LecturerResponse> update(LecturerRequest lecturerRequest){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Lecturer lecturerExit = lecturerRepository.findById(lecturerRequest.getId())
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));
        lecturerExit.setLecturerId(lecturerRequest.getLecturerId());
        lecturerExit.setLecturerName(lecturerRequest.getLecturerName());
        lecturerExit.setLecturerMail(lecturerRequest.getLecturerMail());
        lecturerExit.setLecturerPhone(lecturerRequest.getLecturerPhone());
        lecturerRepository.save(lecturerExit);
        return new ResponseEntity<>(LecturerResponse.builder()
                .lecturerId(lecturerExit.getLecturerId())
                .lecturerName(lecturerExit.getLecturerName())
                .lecturerMail(lecturerExit.getLecturerMail())
                .lecturerPhone(lecturerExit.getLecturerPhone())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<?> addSubject(Long lecturerId, Set<Long> subjectIds){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Set<Subject> subjects = new HashSet<>();
        for (Long subjectId : subjectIds) {
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            subjects.add(subject);
        }
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));
        for (Subject subject : subjects){
            lecturer.getSubjects().add(subject);
            subject.getLecturers().add(lecturer);
        }
        lecturerRepository.save(lecturer);
        return ResponseEntity.ok("Update successful");
    }

    public ResponseEntity<?> deleteSubject(Long lecturerId,Long subjectId){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(()-> new RuntimeException("Subject not found"));
        lecturer.getSubjects().remove(subject);
        subject.getLecturers().remove(lecturer);
        lecturerRepository.save(lecturer);
        subjectRepository.save(subject);
        return ResponseEntity.ok("Xoa thanh cong");
    }
}
