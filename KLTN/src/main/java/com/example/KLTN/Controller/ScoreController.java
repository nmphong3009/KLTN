package com.example.KLTN.Controller;

import com.example.KLTN.DTOS.Response.GpaDTO;
import com.example.KLTN.DTOS.Response.ScoreResponseDTO;
import com.example.KLTN.Service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/scores")
public class ScoreController {
    @Autowired
    private ScoreService scoreService;

    @PostMapping("/add/{subjectId}")
    public ResponseEntity<?> addScore(@PathVariable Long subjectId, @RequestParam Double grade, @RequestParam Long semesterId, @RequestParam Long lecturerId) {
        return ResponseEntity.ok(scoreService.addScore(subjectId,grade,semesterId,lecturerId));
    }

    @PutMapping("/update/{subjectId}")
    public ResponseEntity<?> updateScore(@PathVariable Long subjectId, @RequestParam Double grade) {
        return ResponseEntity.ok(scoreService.updateScore(subjectId,grade));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteScore(@PathVariable Long id){
        return ResponseEntity.ok(scoreService.deleteScore(id));
    }

    @GetMapping("get")
    public ResponseEntity<Map<String, List<ScoreResponseDTO>>> getScore() {
        Map<String, List<ScoreResponseDTO>> scoreResponseDTOS = scoreService.getScore(); // Nhận kết quả đã nhóm theo kỳ học
        return ResponseEntity.ok(scoreResponseDTOS); // Trả về kết quả dạng Map
    }
    @GetMapping("gpa")
    public ResponseEntity<GpaDTO> calculateGPA() {
        return scoreService.calculateGPA();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportScoresToDoc() throws IOException {
        return scoreService.exportScoresToDoc();
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, String>> importScores(@RequestParam("file") MultipartFile file) throws Exception {
        scoreService.importScoresFromDocx(file);

        // Trả về JSON
        Map<String, String> response = new HashMap<>();
        response.put("message", "File uploaded and data imported successfully.");
        return ResponseEntity.ok(response);
    }

}
