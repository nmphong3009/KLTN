package com.example.KLTN.Controller;

import com.example.KLTN.DTOS.Response.ScoreResponseDTO;
import com.example.KLTN.Service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/scores")
public class ScoreController {
    @Autowired
    private ScoreService scoreService;

    @PostMapping("/add/{subjectId}")
    public ResponseEntity<?> addScore(@PathVariable Long subjectId, @RequestParam Double grade) {
        return ResponseEntity.ok(scoreService.addScore(subjectId,grade));
    }

    @PutMapping("/update/{subjectId}")
    public ResponseEntity<?> updateScore(@PathVariable Long subjectId, @RequestParam Double grade) {
        return ResponseEntity.ok(scoreService.updateScore(subjectId,grade));
    }

    @GetMapping("get")
    public ResponseEntity<List<ScoreResponseDTO>> getScore(){
        List<ScoreResponseDTO> scoreResponseDTOS = scoreService.getScore();
        return ResponseEntity.ok(scoreResponseDTOS);
    }

    @GetMapping("gpa")
    public ResponseEntity<?> calculateGPA() {
        return ResponseEntity.ok(scoreService.calculateGPA());
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportScoresToDoc() throws IOException {
        return scoreService.exportScoresToDoc();
    }
}
