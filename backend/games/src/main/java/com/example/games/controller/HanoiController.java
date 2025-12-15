package com.example.games.controller;

import com.example.games.dto.HanoiDTOs.*;
import com.example.games.service.HanoiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hanoi")
@CrossOrigin(origins = "*")
public class HanoiController {

    @Autowired
    private HanoiService service;

    @PostMapping("/start")
    public ResponseEntity<HanoiGameData> startGame(@RequestBody StartGameRequest request) {
        if (request.getPlayerName() == null || request.getPlayerName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getNumPegs() != 3 && request.getNumPegs() != 4) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.startGame(request.getPlayerName(), request.getNumPegs()));
    }

    @PostMapping("/solve/{gameId}")
    public ResponseEntity<HanoiResultResponse> solveGame(@PathVariable String gameId, @RequestBody SolveRequest request) {
        try {
            return ResponseEntity.ok(service.solveGame(gameId, request.getUserMinMoves(), request.getUserSequence()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
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
