package com.example.KLTN.Controller;

import com.example.KLTN.Service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/semester")
public class SemesterController {
    @Autowired
    private SemesterService semesterService;

    @PostMapping("create")
    public ResponseEntity<?> createSemester(@RequestBody String semesterName){
        return ResponseEntity.ok(semesterService.createSemester(semesterName));
    }
}
