package com.example.games.repository;

import com.example.games.entity.EightQueensStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EightQueensStatsRepository extends JpaRepository<EightQueensStats, Long> {
    EightQueensStats findTopByOrderByRunAtDesc();
    List<EightQueensStats> findTop15ByOrderByRunAtDesc();
}
