package com.example.KLTN.Controller;

import com.example.KLTN.DTOS.Request.FacultyRequest;
import com.example.KLTN.DTOS.Request.MajorRequest;
import com.example.KLTN.DTOS.Response.FacultyResponse;
import com.example.KLTN.DTOS.Response.MajorResponse;
import com.example.KLTN.Service.FacultyService;
import com.example.KLTN.Service.MajorService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/major")
public class MajorController {
    @Lazy
    private MajorService majorService;

    @PostMapping("/admin/create")
    public ResponseEntity<MajorResponse> create(@RequestBody MajorRequest majorRequest) {
        return majorService.create(majorRequest);
    }

    @PutMapping("/admin/update")
    public ResponseEntity<MajorResponse> update(@RequestBody MajorRequest majorRequest) {
        return majorService.update(majorRequest);
    }

    @DeleteMapping("admin/delete/{majorId}")
    public ResponseEntity<?> delete(@PathVariable Long majorId){
        return ResponseEntity.ok(majorService.delete(majorId));
    }

    @GetMapping("admin/getAll")
    public ResponseEntity<List<MajorResponse>> getAll(){
        List<MajorResponse> majorResponses = majorService.getAll();
        return ResponseEntity.ok(majorResponses);
    }
}
