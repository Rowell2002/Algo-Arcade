package com.example.games.service;

import com.example.games.dto.SnakeLadderDTOs.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SnakeLadderService
 * Tests BFS and Dijkstra algorithms for finding minimum dice throws
 * Uses reflection to access private methods
 */
class SnakeLadderServiceTest {

    private SnakeLadderService service;
    private Method solveBFSMethod;
    private Method solveDijkstraMethod;

    @BeforeEach
    void setUp() throws Exception {
        service = new SnakeLadderService();
        
        // Access private methods via reflection
        solveBFSMethod = SnakeLadderService.class.getDeclaredMethod("solveBFS", GameData.class);
        solveBFSMethod.setAccessible(true);
        
        solveDijkstraMethod = SnakeLadderService.class.getDeclaredMethod("solveDijkstra", GameData.class);
        solveDijkstraMethod.setAccessible(true);
    }

    // ==================== BFS Algorithm Tests ====================

    @Test
    @DisplayName("BFS: Simple board without snakes or ladders (10x10 = 100 squares)")
    void testBFS_SimpleBoardNoObstacles() throws Exception {
        GameData mockGame = createMockGameData(10, new HashMap<>(), new HashMap<>());
        int result = (int) solveBFSMethod.invoke(service, mockGame);
        
        assertTrue(result > 0, "BFS should return a positive number of throws");
        assertEquals(17, result, "Minimum throws for 100-square board without obstacles is 17");
    }

    @Test
    @DisplayName("BFS: Board with single ladder (shortcut)")
    void testBFS_BoardWithLadder() throws Exception {
        Map<Integer, Integer> ladders = new HashMap<>();
        ladders.put(2, 90); // Ladder from 2 to 90
        
        GameData mockGame = createMockGameData(10, new HashMap<>(), ladders);
        int result = (int) solveBFSMethod.invoke(service, mockGame);
        
        assertTrue(result <= 3, "Ladder should significantly reduce moves");
    }

    @Test
    @DisplayName("BFS: Small board (5x5 = 25 squares)")
    void testBFS_SmallBoard() throws Exception {
        GameData mockGame = createMockGameData(5, new HashMap<>(), new HashMap<>());
        int result = (int) solveBFSMethod.invoke(service, mockGame);
        
        assertEquals(4, result, "Minimum throws for 25-square board is 4 (ceil(24/6))");
    }

    // ==================== Dijkstra Algorithm Tests ====================

    @Test
    @DisplayName("Dijkstra: Simple board without snakes or ladders")
    void testDijkstra_SimpleBoardNoObstacles() throws Exception {
        GameData mockGame = createMockGameData(10, new HashMap<>(), new HashMap<>());
        int result = (int) solveDijkstraMethod.invoke(service, mockGame);
        
        assertTrue(result > 0, "Dijkstra should return a positive number of throws");
        assertEquals(17, result, "Minimum throws for 100-square board without obstacles is 17");
    }

    @Test
    @DisplayName("Dijkstra: Board with single ladder")
    void testDijkstra_BoardWithLadder() throws Exception {
        Map<Integer, Integer> ladders = new HashMap<>();
        ladders.put(2, 90);
        
        GameData mockGame = createMockGameData(10, new HashMap<>(), ladders);
        int result = (int) solveDijkstraMethod.invoke(service, mockGame);
        
        assertTrue(result <= 3, "Ladder should significantly reduce moves");
    }

    @Test
    @DisplayName("Dijkstra: Small board")
    void testDijkstra_SmallBoard() throws Exception {
        GameData mockGame = createMockGameData(5, new HashMap<>(), new HashMap<>());
        int result = (int) solveDijkstraMethod.invoke(service, mockGame);
        
        assertEquals(4, result, "Minimum throws for 25-square board is 4");
    }

    // ==================== BFS vs Dijkstra Consistency ====================

    @Test
    @DisplayName("BFS and Dijkstra should return same result")
    void testBFS_Dijkstra_Consistency() throws Exception {
        Map<Integer, Integer> snakes = new HashMap<>();
        snakes.put(50, 10);
        snakes.put(75, 25);
        
        Map<Integer, Integer> ladders = new HashMap<>();
        ladders.put(5, 45);
        ladders.put(30, 60);
        
        GameData mockGame = createMockGameData(10, snakes, ladders);
        
        int bfsResult = (int) solveBFSMethod.invoke(service, mockGame);
        int dijkstraResult = (int) solveDijkstraMethod.invoke(service, mockGame);
        
        assertEquals(bfsResult, dijkstraResult, "BFS and Dijkstra should produce same result");
    }

    @Test
    @DisplayName("Multiple runs should be deterministic")
    void testAlgorithm_Consistency() throws Exception {
        GameData mockGame = createMockGameData(10, new HashMap<>(), new HashMap<>());
        
        int result1 = (int) solveBFSMethod.invoke(service, mockGame);
        int result2 = (int) solveBFSMethod.invoke(service, mockGame);
        
        assertEquals(result1, result2, "Algorithm should be deterministic");
    }

    // ==================== Helper Methods ====================

    private GameData createMockGameData(int n, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
        GameData game = new GameData();
        game.setGameId(UUID.randomUUID().toString());
        game.setPlayerName("TestPlayer");
        game.setBoardSize(n);
        game.setSnakes(snakes);
        game.setLadders(ladders);
        return game;
    }
}
