package com.example.games.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "eight_queens_solutions")
public class EightQueensSolution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Canonical representation of solution: 8 digits, each approx column index for row 0..7
    // Example: "04752613" means Row 0 is at Col 0, Row 1 at Col 4...
    @Column(unique = true)
    private String solutionString;

    private String discoveredBy; // Player Name
    
    private LocalDateTime discoveredAt = LocalDateTime.now();
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSolutionString() { return solutionString; }
    public void setSolutionString(String solutionString) { this.solutionString = solutionString; }
    public String getDiscoveredBy() { return discoveredBy; }
    public void setDiscoveredBy(String discoveredBy) { this.discoveredBy = discoveredBy; }
    public LocalDateTime getDiscoveredAt() { return discoveredAt; }
    public void setDiscoveredAt(LocalDateTime discoveredAt) { this.discoveredAt = discoveredAt; }
}
