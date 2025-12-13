package com.example.games.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "traffic_results")
public class TrafficGameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerName;
    
    private int maxFlow;      // The correct answer
    private int userGuess;

    private Long fordFulkersonTime; // microseconds
    private Long edmondsKarpTime;   // microseconds

    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public int getMaxFlow() { return maxFlow; }
    public void setMaxFlow(int maxFlow) { this.maxFlow = maxFlow; }
    public int getUserGuess() { return userGuess; }
    public void setUserGuess(int userGuess) { this.userGuess = userGuess; }
    public Long getFordFulkersonTime() { return fordFulkersonTime; }
    public void setFordFulkersonTime(Long fordFulkersonTime) { this.fordFulkersonTime = fordFulkersonTime; }
    public Long getEdmondsKarpTime() { return edmondsKarpTime; }
    public void setEdmondsKarpTime(Long edmondsKarpTime) { this.edmondsKarpTime = edmondsKarpTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
