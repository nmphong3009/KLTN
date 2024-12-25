package com.example.KLTN.Service;

import com.example.KLTN.Entity.Semester;
import com.example.KLTN.Repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SemesterService {
    public final UserService userService;
    public final SemesterRepository semesterRepository;

    public ResponseEntity<?> createSemester(String semesterName){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Semester semester1 = semesterRepository.findBySemesterName(semesterName);
        if (semester1 != null) {
            throw new RuntimeException("Kì học đã tồn tại");
        }
        Semester semester = Semester.builder()
                .semesterName(semesterName)
                .build();
        semesterRepository.save(semester);
        return ResponseEntity.ok("Create Semester successful!");
    }
}
