package com.example.games.repository;

import com.example.games.entity.SnakeLadderGameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnakeLadderRepository extends JpaRepository<SnakeLadderGameResult, Long> {
    List<SnakeLadderGameResult> findTop15ByOrderByCreatedAtDesc();

    // Leaderboard: Get top 20 players ordered by board size (desc) and BFS time (asc)
    List<SnakeLadderGameResult> findTop20ByPlayerNameIsNotNullOrderByBoardSizeDescBfsTimeAsc();
}
