package com.example.games.controller;

import com.example.games.dto.TrafficDTOs.*;
import com.example.games.service.TrafficService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/traffic")
@CrossOrigin(origins = "*")
public class TrafficController {

    @Autowired
    private TrafficService service;

    @PostMapping("/start")
    public ResponseEntity<TrafficGameData> startGame(@RequestBody StartGameRequest request) {
        if (request.getPlayerName() == null || request.getPlayerName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.startGame(request.getPlayerName()));
    }

    @PostMapping("/solve/{gameId}")
    public ResponseEntity<?> solveGame(@PathVariable String gameId, @RequestBody SolveRequest request) {
        try {
            return ResponseEntity.ok(service.solveGame(gameId, request.getUserGuess()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
