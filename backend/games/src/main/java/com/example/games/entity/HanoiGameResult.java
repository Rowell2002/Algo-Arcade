package com.example.games.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hanoi_results")
public class HanoiGameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerName;
    
    private int numDisks;
    private int numPegs;
    
    // User response
    private int userMinMoves; // Estimated moves
    
    @Column(columnDefinition = "TEXT")
    private String userSequence; // For larger N, this can be big
    
    
    // Correct answers
    private int optimalMinMoves;

    // Algorithm timings
    private Long algo1Time; // Recursive (3) or Frame-Stewart (4)
    private Long algo2Time; // Iterative (3) or BFS (4)

    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public int getNumDisks() { return numDisks; }
    public void setNumDisks(int numDisks) { this.numDisks = numDisks; }
    public int getNumPegs() { return numPegs; }
    public void setNumPegs(int numPegs) { this.numPegs = numPegs; }
    public int getUserMinMoves() { return userMinMoves; }
    public void setUserMinMoves(int userMinMoves) { this.userMinMoves = userMinMoves; }
    public String getUserSequence() { return userSequence; }
    public void setUserSequence(String userSequence) { this.userSequence = userSequence; }
    public int getOptimalMinMoves() { return optimalMinMoves; }
    public void setOptimalMinMoves(int optimalMinMoves) { this.optimalMinMoves = optimalMinMoves; }
    public Long getAlgo1Time() { return algo1Time; }
    public void setAlgo1Time(Long algo1Time) { this.algo1Time = algo1Time; }
    public Long getAlgo2Time() { return algo2Time; }
    public void setAlgo2Time(Long algo2Time) { this.algo2Time = algo2Time; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
