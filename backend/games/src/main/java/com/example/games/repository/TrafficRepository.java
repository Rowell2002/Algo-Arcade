package com.example.games.repository;

import com.example.games.entity.TrafficGameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrafficRepository extends JpaRepository<TrafficGameResult, Long> {
    List<TrafficGameResult> findTop15ByOrderByCreatedAtDesc();
}
