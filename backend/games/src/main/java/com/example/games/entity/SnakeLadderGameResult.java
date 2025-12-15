package com.example.games.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "snake_ladder_results")
public class SnakeLadderGameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerName;
    private int boardSize;

    private int minDiceThrows; // The calculated correct answer
    private int userGuess;

    private Long bfsTime;      // Time in microseconds
    private Long dijkstraTime; // Time in microseconds

    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    
    public int getBoardSize() { return boardSize; }
    public void setBoardSize(int boardSize) { this.boardSize = boardSize; }

    public int getMinDiceThrows() { return minDiceThrows; }
    public void setMinDiceThrows(int minDiceThrows) { this.minDiceThrows = minDiceThrows; }

    public int getUserGuess() { return userGuess; }
    public void setUserGuess(int userGuess) { this.userGuess = userGuess; }


    public Long getBfsTime() { return bfsTime; }
    public void setBfsTime(Long bfsTime) { this.bfsTime = bfsTime; }

    public Long getDijkstraTime() { return dijkstraTime; }
    public void setDijkstraTime(Long dijkstraTime) { this.dijkstraTime = dijkstraTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
