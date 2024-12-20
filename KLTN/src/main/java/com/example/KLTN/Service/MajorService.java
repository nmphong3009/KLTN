package com.example.KLTN.Service;

import com.example.KLTN.DTOS.Request.MajorRequest;
import com.example.KLTN.DTOS.Response.MajorResponse;
import com.example.KLTN.Entity.Faculty;
import com.example.KLTN.Entity.Major;
import com.example.KLTN.Repository.FacultyRepository;
import com.example.KLTN.Repository.MajorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MajorService {
    private final MajorRepository majorRepository;
    private final UserService userService;
    private final FacultyRepository facultyRepository;

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
}
