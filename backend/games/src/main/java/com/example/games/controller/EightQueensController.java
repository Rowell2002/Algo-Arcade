package com.example.games.controller;

import com.example.games.dto.EightQueensDTOs.*;
import com.example.games.service.EightQueensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eight-queens")
@CrossOrigin(origins = "*")
public class EightQueensController {

    @Autowired
    private EightQueensService service;

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(service.getStats());
    }

    @PostMapping("/submit")
    public ResponseEntity<SubmitResponse> submitSolution(@RequestBody SubmitRequest request) {
        if (request.getPlayerName() == null || request.getPlayerName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getQueens() == null || request.getQueens().size() != 8) {
             return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.submitSolution(request));
    }
    
    @GetMapping("/comparison")
    public ResponseEntity<?> getComparison() {
        try {
            return ResponseEntity.ok(service.getComparisonData());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
