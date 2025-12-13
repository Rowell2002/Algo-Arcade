package com.example.games.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tsp_results")
public class TspGameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerName;
    private String homeCity;
    
    // Which cities were Selected to visit (comma separated or JSON)
    @Column(columnDefinition = "TEXT")
    private String selectedCities; // e.g. "B,C,F"
    
    // The optimal path found (e.g. "A -> B -> F -> C -> A")
    @Column(columnDefinition = "TEXT")
    private String optimalPath;
    
    private int minDistance;
    private int userDistance;

    // Execution times in nanoseconds
    private Long bruteForceTimeNs;
    private Long nearestNeighborTimeNs;
    private Long dynamicProgrammingTimeNs;
    
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getHomeCity() { return homeCity; }
    public void setHomeCity(String homeCity) { this.homeCity = homeCity; }
    public String getSelectedCities() { return selectedCities; }
    public void setSelectedCities(String selectedCities) { this.selectedCities = selectedCities; }
    public String getOptimalPath() { return optimalPath; }
    public void setOptimalPath(String optimalPath) { this.optimalPath = optimalPath; }
    public int getMinDistance() { return minDistance; }
    public void setMinDistance(int minDistance) { this.minDistance = minDistance; }
    public int getUserDistance() { return userDistance; }
    public void setUserDistance(int userDistance) { this.userDistance = userDistance; }
    public Long getBruteForceTimeNs() { return bruteForceTimeNs; }
    public void setBruteForceTimeNs(Long bruteForceTimeNs) { this.bruteForceTimeNs = bruteForceTimeNs; }
    public Long getNearestNeighborTimeNs() { return nearestNeighborTimeNs; }
    public void setNearestNeighborTimeNs(Long nearestNeighborTimeNs) { this.nearestNeighborTimeNs = nearestNeighborTimeNs; }
    public Long getDynamicProgrammingTimeNs() { return dynamicProgrammingTimeNs; }
    public void setDynamicProgrammingTimeNs(Long dynamicProgrammingTimeNs) { this.dynamicProgrammingTimeNs = dynamicProgrammingTimeNs; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
