package com.example.games.repository;

import com.example.games.entity.TspGameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TspRepository extends JpaRepository<TspGameResult, Long> {
    List<TspGameResult> findTop15ByOrderByCreatedAtDesc();
}
