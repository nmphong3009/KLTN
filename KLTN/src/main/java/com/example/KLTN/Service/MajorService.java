package com.example.KLTN.Service;

import com.example.KLTN.DTOS.Request.MajorRequest;
import com.example.KLTN.DTOS.Response.MajorResponse;
import com.example.KLTN.DTOS.Response.SubjectResponseDTO;
import com.example.KLTN.Entity.*;
import com.example.KLTN.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MajorService {
    private final MajorRepository majorRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final SubjectRepository subjectRepository;
    private final MajorSubjectRepository majorSubjectRepository;
    private final ScoreRepository scoreRepository;


    public ResponseEntity<MajorResponse> create(MajorRequest majorRequest){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        if (majorRepository.findByMajorName(majorRequest.getMajorName()).isPresent())
            throw new RuntimeException("Major already exists!");
        Faculty faculty = facultyRepository.findById(majorRequest.getFacultyId())
                .orElseThrow(()-> new RuntimeException("Faculty not found"));
        Major major = Major.builder()
                .majorName(majorRequest.getMajorName())
                .faculty(faculty)
                .build();
        majorRepository.save(major);
        return new ResponseEntity<>(MajorResponse.builder()
                .id(major.getId())
                .majorName(major.getMajorName())
                .facultyName(major.getFaculty().getFacultyName())
                .build(), HttpStatus.CREATED);
    }

    public ResponseEntity<MajorResponse> update(MajorRequest majorRequest){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Faculty faculty = facultyRepository.findById(majorRequest.getFacultyId())
                .orElseThrow(()-> new RuntimeException("Faculty not found"));
        Major major = majorRepository.findById(majorRequest.getId())
                .orElseThrow(()-> new RuntimeException("Major not found"));
        major.setMajorName(majorRequest.getMajorName());
        major.setFaculty(faculty);
        majorRepository.save(major);
        return new ResponseEntity<>(MajorResponse.builder()
                .id(major.getId())
                .majorName(major.getMajorName())
                .facultyName(major.getFaculty().getFacultyName())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<?> delete(Long majorId){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Major major = majorRepository.findById(majorId)
                .orElseThrow(()-> new RuntimeException("Major not found"));
        majorRepository.delete(major);
        return ResponseEntity.ok("Delete Major successful");
    }

    public List<MajorResponse> getAll() {
        List<Major> majorList = majorRepository.findAll();
        return majorList.stream().map(
                major -> MajorResponse.builder()
                        .id(major.getId())
                        .majorName(major.getMajorName())
                        .facultyName(major.getFaculty().getFacultyName())
                        .build()
        ).toList();
    }

    public List<SubjectResponseDTO> getAllSubject(Long majorId){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Major major = majorRepository.findById(majorId)
                .orElseThrow(()-> new RuntimeException("Major not found"));
        List<MajorSubject> majorSubjects = majorSubjectRepository.findByMajor(major);
        List<Subject> subjects = majorSubjects.stream()
                .map(MajorSubject::getSubject) // Lấy Subject từ MajorSubject
                .collect(Collectors.toList());
        return subjects.stream()
                .map(subject -> SubjectResponseDTO.builder()
                        .id(subject.getId())
                        .subjectId(subject.getSubjectId())
                        .subjectName(subject.getSubjectName())
                        .credit(subject.getCredit())
                        .build())
                .collect(Collectors.toList());
    }

    public List<SubjectResponseDTO> userGetAllSubject(){
        User user = userService.getCurrentUser();
        Long majorId = user.getMajor().getId();
        Major major = majorRepository.findById(majorId)
                .orElseThrow(()-> new RuntimeException("Major not found"));
        List<MajorSubject> majorSubjects = majorSubjectRepository.findByMajor(major);
        List<Subject> subject1 = majorSubjects.stream()
                .map(MajorSubject::getSubject) // Lấy Subject từ MajorSubject
                .collect(Collectors.toList());
        List<Score> scoreList = scoreRepository.findByUser(user);
        List<Subject> subject2 = scoreList.stream()
                .map(Score::getSubject)
                .collect(Collectors.toList());
        return subject1.stream()
                .map(subject -> {
                    boolean hasStudied = checkUserSubject(subject, subject2);
                    return SubjectResponseDTO.builder()
                            .id(subject.getId())
                            .subjectId(subject.getSubjectId())
                            .subjectName(subject.getSubjectName())
                            .credit(subject.getCredit())
                            .hasStudied(hasStudied)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public boolean checkUserSubject(Subject subject, List<Subject> subjectList){
        for (Subject s : subjectList) {
            if (s.getId() == subject.getId()) {
                return true;
            }
        }
        return false;
    }

    public ResponseEntity<?> addSubject(Long majorId, Long subjectId){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Major major = majorRepository.findById(majorId)
                .orElseThrow(()-> new RuntimeException("Major not found"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(()-> new RuntimeException("Subject not found"));
        MajorSubject majorSubjects = majorSubjectRepository.findByMajorAndSubject(major,subject);
        if (majorSubjects == null){
            MajorSubject majorSubject = MajorSubject.builder()
                    .major(major)
                    .subject(subject)
                    .build();
            majorSubjectRepository.save(majorSubject);
            return ResponseEntity.ok("Add Subject successful");
        } else {
            throw new RuntimeException("Subject is not already exit");
        }
    }
    public ResponseEntity<?> deleteSubject(Long majorId, Long subjectId){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Major major = majorRepository.findById(majorId)
                .orElseThrow(()-> new RuntimeException("Major not found"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(()-> new RuntimeException("Subject not found"));
        MajorSubject majorSubjects = majorSubjectRepository.findByMajorAndSubject(major,subject);
        if (majorSubjects == null){
            throw new RuntimeException("Subject is not already exit");
        } else {
            majorSubjectRepository.delete(majorSubjects);
            return ResponseEntity.ok("Delete Subject successful");
        }
    }
}
