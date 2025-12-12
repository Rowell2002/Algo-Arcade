package com.example.games.controller;

import com.example.games.dto.TspDTOs.*;
import com.example.games.service.TspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tsp")
@CrossOrigin(origins = "*")
public class TspController {

    @Autowired
    private TspService service;

    @PostMapping("/start")
    public ResponseEntity<TspGameData> startGame(@RequestBody StartGameRequest request) {
        return ResponseEntity.ok(service.startGame(request));
    }

    @PostMapping("/solve/{gameId}")
    public ResponseEntity<TspResult> solveGame(@PathVariable String gameId, @RequestBody SolveTspRequest request) {
        try {
            return ResponseEntity.ok(service.solveGame(gameId, request));
        } catch (RuntimeException e) {
             TspResult err = new TspResult();
             err.setMessage("Error: " + e.getMessage());
             return ResponseEntity.badRequest().body(err);
        }
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
