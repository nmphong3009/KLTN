package com.example.KLTN.Service;

import com.example.KLTN.DTOS.Request.SubjectRequest;
import com.example.KLTN.DTOS.Response.LecturerResponse;
import com.example.KLTN.DTOS.Response.SubjectResponseDTO;
import com.example.KLTN.Entity.*;
import com.example.KLTN.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class SubjectService {
    private final UserService userService;
    private final SubjectRepository subjectRepository;
    private final MajorRepository majorRepository;
    private final MajorSubjectRepository majorSubjectRepository;
    private final LecturerRepository lecturerRepository;
    private final ScoreRepository scoreRepository;

    public ResponseEntity<SubjectResponseDTO> createSubject(SubjectRequest subjectRequest) {
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        List<Major> majors = majorRepository.findAllById(subjectRequest.getMajorIds());

        Subject subjectExit = subjectRepository.findBySubjectId(subjectRequest.getSubjectId());
        if (subjectExit != null) {
            throw new RuntimeException("SubjectId is not exit");
        }
        Subject subject = Subject.builder()
                .subjectId(subjectRequest.getSubjectId())
                .subjectName(subjectRequest.getSubjectName())
                .credit(subjectRequest.getCredit())
                .build();
        subjectRepository.save(subject);
        majors.forEach(major -> {
            MajorSubject majorSubject = MajorSubject.builder()
                    .subject(subject)
                    .major(major)
                    .build();
            majorSubjectRepository.save(majorSubject);
        });
        List<String> majorNames = majors.stream()
                .map(Major::getMajorName)
                .collect(Collectors.toList());
        // Trả về SubjectResponseDTO
        return new ResponseEntity<>(SubjectResponseDTO.builder()
                .subjectId(subject.getSubjectId())
                .subjectName(subject.getSubjectName())
                .credit(subject.getCredit())
                .id(subject.getId())
                .majorName(majorNames)
                .build(), HttpStatus.CREATED);
    }

    public ResponseEntity<SubjectResponseDTO> updateSubject(SubjectRequest subjectRequest){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Subject subject = subjectRepository.findById(subjectRequest.getId())
                .orElseThrow(()-> new RuntimeException("Subject not found"));

        subject.setSubjectId(subjectRequest.getSubjectId());
        subject.setSubjectName(subject.getSubjectName());
        subject.setCredit(subject.getCredit());
        subjectRepository.save(subject);
        return new ResponseEntity<>(SubjectResponseDTO.builder()
                .subjectId(subject.getSubjectId())
                .subjectName(subject.getSubjectName())
                .credit(subject.getCredit())
                .id(subject.getId())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteSubject(Long id){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Subject not found"));
        for (Lecturer lecturer : subject.getLecturers()){
            lecturer.getSubjects().remove(subject);
        }
        subjectRepository.delete(subject);
        return ResponseEntity.ok("Delete Subject successful!");
    }

    public List<SubjectResponseDTO> getAllSubject(){
        List<Subject> subjectList = subjectRepository.findAll();
        return subjectList.stream().map(
                subject -> SubjectResponseDTO.builder()
                        .id(subject.getId())
                        .subjectId(subject.getSubjectId())
                        .subjectName(subject.getSubjectName())
                        .credit(subject.getCredit())
                        .build()
        ).toList();
    }

    public ResponseEntity<SubjectResponseDTO> getSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Subject not found"));
        return new ResponseEntity<>(SubjectResponseDTO.builder()
                .subjectId(subject.getSubjectId())
                .subjectName(subject.getSubjectName())
                .credit(subject.getCredit())
                .id(subject.getId())
                .build(), HttpStatus.OK);
    }

    public List<LecturerResponse> getAlLecturer(Long id){
        List<Lecturer> lecturers = lecturerRepository.findBySubjectId(id);
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Subject not found"));
        return lecturers.stream().map(
                lecturer -> {
                    // Tìm danh sách điểm của giảng viên cho môn học hiện tại
                    List<Score> scores = scoreRepository.findByLecturerAndSubject(lecturer, subject);

                    // Tính điểm trung bình
                    double averageScore = scores.stream()
                            .mapToDouble(Score::getGrade) // Lấy giá trị điểm
                            .average() // Tính trung bình
                            .orElse(0.0); // Nếu không có điểm thì trả về 0.0

                    // Tạo LecturerResponse với thông tin và điểm trung bình
                    return LecturerResponse.builder()
                            .id(lecturer.getId())
                            .lecturerId(lecturer.getLecturerId())
                            .lecturerName(lecturer.getLecturerName())
                            .lecturerMail(lecturer.getLecturerMail())
                            .lecturerPhone(lecturer.getLecturerPhone())
                            .averageScore(averageScore) // Gán điểm trung bình
                            .build();
                }
        ).toList();
    }

    public ResponseEntity<?> deleteLecturer(Long lecturerId,Long subjectId){
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

    public ResponseEntity<?> addLecturers(Set<Long> lecturerIds, Long subjectId) {
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Set<Lecturer> lecturers = new HashSet<>();
        for (Long lecturerId : lecturerIds) {
            Lecturer lecturer = lecturerRepository.findById(lecturerId)
                    .orElseThrow(() -> new RuntimeException("Lecturer not found"));
            lecturers.add(lecturer);
        }
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        for (Lecturer lecturer : lecturers) {
            subject.getLecturers().add(lecturer);
            lecturer.getSubjects().add(subject);
        }
        subjectRepository.save(subject);
        return ResponseEntity.ok("Update successful");
    }
}
