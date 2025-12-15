package com.example.games.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "eight_queens_stats")
public class EightQueensStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sequentialTimeNs;
    private Long threadedTimeNs;
    
    private LocalDateTime runAt = LocalDateTime.now();
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSequentialTimeNs() { return sequentialTimeNs; }
    public void setSequentialTimeNs(Long sequentialTimeNs) { this.sequentialTimeNs = sequentialTimeNs; }
    public Long getThreadedTimeNs() { return threadedTimeNs; }
    public void setThreadedTimeNs(Long threadedTimeNs) { this.threadedTimeNs = threadedTimeNs; }
    public LocalDateTime getRunAt() { return runAt; }
    public void setRunAt(LocalDateTime runAt) { this.runAt = runAt; }
}
