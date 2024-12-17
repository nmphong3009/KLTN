package com.example.KLTN.Controller;

import com.example.KLTN.DTOS.Request.SubjectRequest;
import com.example.KLTN.DTOS.Response.SubjectResponseDTO;
import com.example.KLTN.Service.SubjectService;
import com.example.KLTN.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
