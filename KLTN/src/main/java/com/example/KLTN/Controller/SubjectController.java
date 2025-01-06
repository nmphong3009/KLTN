package com.example.KLTN.Controller;

import com.example.KLTN.DTOS.Request.SubjectRequest;
import com.example.KLTN.DTOS.Response.LecturerResponse;
import com.example.KLTN.DTOS.Response.SubjectResponseDTO;
import com.example.KLTN.Service.SubjectService;
import com.example.KLTN.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/subject")

public class SubjectController {
    @Lazy
    private SubjectService subjectService;

    @PostMapping("createSubject")
    public ResponseEntity<SubjectResponseDTO> createSubject(@RequestBody SubjectRequest subjectRequest){
        return subjectService.createSubject(subjectRequest);
    }

    @PutMapping("updateSubject")
    public ResponseEntity<SubjectResponseDTO> updateSubject(@RequestBody SubjectRequest subjectRequest){
        return subjectService.updateSubject(subjectRequest);
    }

    @GetMapping("getSubject/{id}")
    public ResponseEntity<SubjectResponseDTO> getSubject(@PathVariable Long id) {
        return subjectService.getSubject(id);
    }

    @DeleteMapping("deleteSubject/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id){
        return ResponseEntity.ok(subjectService.deleteSubject(id));
    }

    @GetMapping("/admin/getAll")
    public ResponseEntity<List<SubjectResponseDTO>> getAllSubject(){
        List<SubjectResponseDTO> subjectResponseDTOList = subjectService.getAllSubject();
        return ResponseEntity.ok(subjectResponseDTOList);
    }

    @GetMapping("getAllLecturer/{id}")
    public ResponseEntity<List<LecturerResponse>> getAllLecturer(@PathVariable Long id){
        List<LecturerResponse> lecturerResponses = subjectService.getAlLecturer(id);
        return ResponseEntity.ok(lecturerResponses);
    }

    @DeleteMapping("deleteLecturer")
    public ResponseEntity<?> deleteLecturer(@RequestParam Long lecturerId,@RequestParam Long subjectId){
        return ResponseEntity.ok(subjectService.deleteLecturer(lecturerId, subjectId));
    }

    @PostMapping("addLecturer/{subjectId}")
    public ResponseEntity<?> addLecturer(@RequestBody Set<Long> lecturerIds, @PathVariable Long subjectId) {
        return ResponseEntity.ok(subjectService.addLecturers(lecturerIds, subjectId));
    }
}
