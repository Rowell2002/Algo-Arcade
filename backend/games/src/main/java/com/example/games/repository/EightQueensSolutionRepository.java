package com.example.games.repository;

import com.example.games.entity.EightQueensSolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EightQueensSolutionRepository extends JpaRepository<EightQueensSolution, Long> {
    List<EightQueensSolution> findTop10ByOrderByDiscoveredAtDesc();
    Optional<EightQueensSolution> findBySolutionString(String solutionString);
    long count();
}
