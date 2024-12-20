package com.example.KLTN.Service;

import com.example.KLTN.DTOS.Request.FacultyRequest;
import com.example.KLTN.DTOS.Response.FacultyResponse;
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
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final UserService userService;
    private  final MajorRepository majorRepository;

    public ResponseEntity<FacultyResponse> createFaculty(FacultyRequest facultyRequest){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Faculty faculty = Faculty.builder()
                .facultyName(facultyRequest.getFacultyName())
                .build();
        facultyRepository.save(faculty);
        return new ResponseEntity<>(FacultyResponse.builder()
                .id(faculty.getId())
                .facultyName(faculty.getFacultyName())
                .build(), HttpStatus.CREATED);
    }

    public ResponseEntity<FacultyResponse> updateFaculty(FacultyRequest facultyRequest) {
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Faculty faculty = facultyRepository.findById(facultyRequest.getId())
                .orElseThrow(()-> new RuntimeException("Faculty not found"));
        faculty.setFacultyName(facultyRequest.getFacultyName());
        facultyRepository.save(faculty);
        return new ResponseEntity<>(FacultyResponse.builder()
                .id(faculty.getId())
                .facultyName(faculty.getFacultyName())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteFaculty(Long id){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Faculty not found"));
        facultyRepository.delete(faculty);
        return ResponseEntity.ok("Delete Faculty successful!");
    }

    public List<FacultyResponse> getAll(){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        List<Faculty> faculties = facultyRepository.findAll();
        return faculties.stream().map(
                faculty -> FacultyResponse.builder()
                        .facultyName(faculty.getFacultyName())
                        .id(faculty.getId())
                        .build()
        ).toList();
    }

    public List<MajorResponse> getAllMajor(Long facultyId){
        if (!userService.isAdmin()) {
            throw new RuntimeException("Only admin users can access this resource.");
        }
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(()-> new RuntimeException("Faculty not found"));
        List<Major> majorList = majorRepository.findByFaculty(faculty);
        return majorList.stream().map(
                major -> MajorResponse.builder()
                        .id(major.getId())
                        .majorName(major.getMajorName())
                        .build()
        ).toList();
    }
}
