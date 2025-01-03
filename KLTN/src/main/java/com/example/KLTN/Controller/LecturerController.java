package com.example.KLTN.Controller;

import com.example.KLTN.DTOS.Request.LecturerRequest;
import com.example.KLTN.DTOS.Response.LecturerResponse;
import com.example.KLTN.DTOS.Response.SubjectResponseDTO;
import com.example.KLTN.Service.LecturerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/lecturer")
public class LecturerController {
    public final LecturerService lecturerService;
    @PostMapping("create")
    public ResponseEntity<?> create (@RequestBody LecturerRequest request){
        return ResponseEntity.ok(lecturerService.create(request));
    }
    @DeleteMapping("delete")
    public ResponseEntity<?> delete (@RequestParam Long lecturerId){
        return ResponseEntity.ok(lecturerService.delete(lecturerId));
    }

    @GetMapping("getAll")
    public ResponseEntity<List<LecturerResponse>> getAll(){
        List<LecturerResponse> lecturerResponses = lecturerService.getAllLecturer();
        return ResponseEntity.ok(lecturerResponses);
    }

    @GetMapping("getSubjectLecturer/{id}")
    public ResponseEntity<List<SubjectResponseDTO>> getAllSubject(@PathVariable Long id){
        List<SubjectResponseDTO> subjectResponseDTOS = lecturerService.getAllSubject(id);
        return ResponseEntity.ok(subjectResponseDTOS);
    }

    @PutMapping("update")
    public ResponseEntity<LecturerResponse> update(@RequestBody LecturerRequest lecturerRequest) {
        return lecturerService.update(lecturerRequest);
    }

    @PostMapping("addSubject")
    public ResponseEntity<?> addSubject(@RequestParam Long lecturerId,@RequestParam Long subjectId){
        return ResponseEntity.ok(lecturerService.addSubject(lecturerId, subjectId));
    }

    @DeleteMapping("deleteSubject")
    public ResponseEntity<?> deleteSubject(@RequestParam Long lecturerId,@RequestParam Long subjectId){
        return ResponseEntity.ok(lecturerService.deleteSubject(lecturerId, subjectId));
    }
}
