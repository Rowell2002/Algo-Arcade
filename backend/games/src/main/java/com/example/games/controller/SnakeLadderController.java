package com.example.games.controller;

import com.example.games.dto.SnakeLadderDTOs.*;
import com.example.games.service.SnakeLadderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/snakeladder")
@CrossOrigin(origins = "*")
public class SnakeLadderController {

    @Autowired
    private SnakeLadderService service;

    @PostMapping("/start")
    public ResponseEntity<GameData> startGame(@RequestBody StartGameRequest request) {
        if (request.getBoardSize() < 6 || request.getBoardSize() > 12) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.startGame(request.getPlayerName(), request.getBoardSize()));
    }

    @PostMapping("/solve/{gameId}")
    public ResponseEntity<?> solveGame(@PathVariable String gameId, @RequestBody SolveGameRequest request) {
        try {
            GameResultResponse response = service.solveGame(gameId, request.getUserGuess());
            return ResponseEntity.ok(response);
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

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard() {
        try {
            return ResponseEntity.ok(service.getLeaderboard());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
