package com.example.KLTN.Controller;

import com.example.KLTN.DTOS.Request.FacultyRequest;
import com.example.KLTN.DTOS.Response.FacultyResponse;
import com.example.KLTN.DTOS.Response.MajorResponse;
import com.example.KLTN.Service.FacultyService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/faculty")

public class FacultyController {
    @Lazy
    private FacultyService facultyService;

    @PostMapping("/admin/create")
    public ResponseEntity<FacultyResponse> create(@RequestBody FacultyRequest facultyRequest) {
        return facultyService.createFaculty(facultyRequest);
    }

    @PutMapping("/admin/update")
    public ResponseEntity<FacultyResponse> update(@RequestBody FacultyRequest facultyRequest) {
        return facultyService.updateFaculty(facultyRequest);
    }

    @DeleteMapping("admin/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return ResponseEntity.ok(facultyService.deleteFaculty(id));
    }

    @GetMapping("admin/getAll")
    public ResponseEntity<List<FacultyResponse>> getAll(){
        List<FacultyResponse> facultyResponses = facultyService.getAll();
        return ResponseEntity.ok(facultyResponses);
    }

    @GetMapping("/admin/getAllMajor/{facultyId}")
    public ResponseEntity<List<MajorResponse>> getAllMajor(@PathVariable Long facultyId){
        List<MajorResponse> majorResponses = facultyService.getAllMajor(facultyId);
        return ResponseEntity.ok(majorResponses);
    }
}
