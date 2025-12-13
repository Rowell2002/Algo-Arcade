package com.example.games.repository;

import com.example.games.entity.HanoiGameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HanoiRepository extends JpaRepository<HanoiGameResult, Long> {
    List<HanoiGameResult> findTop15ByOrderByCreatedAtDesc();
}
