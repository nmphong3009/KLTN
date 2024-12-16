package com.example.KLTN.Service;

import com.example.KLTN.DTOS.Request.SubjectRequest;
import com.example.KLTN.DTOS.Response.SubjectResponseDTO;
import com.example.KLTN.Entity.Subject;
import com.example.KLTN.Repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class SubjectService {
    private final UserService userService;
    private final SubjectRepository subjectRepository;

    public ResponseEntity<SubjectResponseDTO> createSubject(SubjectRequest subjectRequest){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Subject subject = Subject.builder()
                .subjectId(subjectRequest.getSubjectId())
                .subjectName(subjectRequest.getSubjectName())
                .credit(subjectRequest.getCredit())
                .build();
        subjectRepository.save(subject);
        return new ResponseEntity<>(SubjectResponseDTO.builder()
                .subjectId(subject.getSubjectId())
                .subjectName(subject.getSubjectName())
                .credit(subject.getCredit())
                .id(subject.getId())
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
