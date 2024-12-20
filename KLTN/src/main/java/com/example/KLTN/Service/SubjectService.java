package com.example.KLTN.Service;

import com.example.KLTN.DTOS.Request.SubjectRequest;
import com.example.KLTN.DTOS.Response.SubjectResponseDTO;
import com.example.KLTN.Entity.Major;
import com.example.KLTN.Entity.MajorSubject;
import com.example.KLTN.Entity.Subject;
import com.example.KLTN.Repository.MajorRepository;
import com.example.KLTN.Repository.MajorSubjectRepository;
import com.example.KLTN.Repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class SubjectService {
    private final UserService userService;
    private final SubjectRepository subjectRepository;
    private final MajorRepository majorRepository;
    private final MajorSubjectRepository majorSubjectRepository;

    public ResponseEntity<SubjectResponseDTO> createSubject(SubjectRequest subjectRequest) {
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        List<Major> majors = majorRepository.findAllById(subjectRequest.getMajorIds());

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
        subjectRepository.delete(subject);
        return ResponseEntity.ok("Delete Subject successful!");
    }

    public List<SubjectResponseDTO> getAllSubject(){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
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
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Subject not found"));
        return new ResponseEntity<>(SubjectResponseDTO.builder()
                .subjectId(subject.getSubjectId())
                .subjectName(subject.getSubjectName())
                .credit(subject.getCredit())
                .id(subject.getId())
                .build(), HttpStatus.OK);
    }
}
