package com.example.games.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HanoiService
 * Tests Recursive, Iterative (3-peg), Frame-Stewart, and BFS (4-peg) algorithms
 * Uses reflection to access private methods
 */
class HanoiServiceTest {

    private HanoiService service;
    private Method solveRecursive3Method;
    private Method solveIterative3Method;
    private Method solveFrameStewartMethod;
    private Method solveBFS4Method;

    @BeforeEach
    void setUp() throws Exception {
        service = new HanoiService();
        
        // Access private methods via reflection
        solveRecursive3Method = HanoiService.class.getDeclaredMethod("solveRecursive3", int.class);
        solveRecursive3Method.setAccessible(true);
        
        solveIterative3Method = HanoiService.class.getDeclaredMethod("solveIterative3", int.class);
        solveIterative3Method.setAccessible(true);
        
        solveFrameStewartMethod = HanoiService.class.getDeclaredMethod("solveFrameStewart", int.class, int.class);
        solveFrameStewartMethod.setAccessible(true);
        
        solveBFS4Method = HanoiService.class.getDeclaredMethod("solveBFS4", int.class);
        solveBFS4Method.setAccessible(true);
    }

    // ==================== 3-Peg Recursive Algorithm Tests ====================

    @Test
    @DisplayName("Recursive 3-peg: 1 disk requires 1 move")
    void testRecursive3_OneDisk() throws Exception {
        int result = (int) solveRecursive3Method.invoke(service, 1);
        assertEquals(1, result, "1 disk requires exactly 1 move");
    }

    @Test
    @DisplayName("Recursive 3-peg: 2 disks require 3 moves")
    void testRecursive3_TwoDisks() throws Exception {
        int result = (int) solveRecursive3Method.invoke(service, 2);
        assertEquals(3, result, "2 disks require exactly 3 moves (2^2 - 1)");
    }

    @Test
    @DisplayName("Recursive 3-peg: 3 disks require 7 moves")
    void testRecursive3_ThreeDisks() throws Exception {
        int result = (int) solveRecursive3Method.invoke(service, 3);
        assertEquals(7, result, "3 disks require exactly 7 moves (2^3 - 1)");
    }

    @Test
    @DisplayName("Recursive 3-peg: N disks follow 2^N - 1 formula")
    void testRecursive3_Formula() throws Exception {
        for (int n = 1; n <= 10; n++) {
            int expected = (int) Math.pow(2, n) - 1;
            int result = (int) solveRecursive3Method.invoke(service, n);
            assertEquals(expected, result, "For " + n + " disks, expected " + expected + " moves");
        }
    }

    // ==================== 3-Peg Iterative Algorithm Tests ====================

    @Test
    @DisplayName("Iterative 3-peg: 1 disk requires 1 move")
    void testIterative3_OneDisk() throws Exception {
        int result = (int) solveIterative3Method.invoke(service, 1);
        assertEquals(1, result, "1 disk requires exactly 1 move");
    }

    @Test
    @DisplayName("Iterative 3-peg: N disks follow 2^N - 1 formula")
    void testIterative3_Formula() throws Exception {
        for (int n = 1; n <= 10; n++) {
            int expected = (int) Math.pow(2, n) - 1;
            int result = (int) solveIterative3Method.invoke(service, n);
            assertEquals(expected, result, "For " + n + " disks, expected " + expected + " moves");
        }
    }

    // ==================== Recursive vs Iterative Consistency ====================

    @Test
    @DisplayName("Recursive and Iterative should return same result")
    void testRecursive_Iterative_Consistency() throws Exception {
        for (int n = 1; n <= 10; n++) {
            int recursive = (int) solveRecursive3Method.invoke(service, n);
            int iterative = (int) solveIterative3Method.invoke(service, n);
            assertEquals(recursive, iterative, "Both algorithms should produce same result for " + n + " disks");
        }
    }

    // ==================== 4-Peg Frame-Stewart Algorithm Tests ====================

    @Test
    @DisplayName("Frame-Stewart 4-peg: 1 disk requires 1 move")
    void testFrameStewart_OneDisk() throws Exception {
        int result = (int) solveFrameStewartMethod.invoke(service, 1, 4);
        assertEquals(1, result, "1 disk requires 1 move even with 4 pegs");
    }

    @Test
    @DisplayName("Frame-Stewart 4-peg: should be faster than 3-peg for larger N")
    void testFrameStewart_FasterThan3Peg() throws Exception {
        for (int n = 3; n <= 10; n++) {
            int threeP = (int) solveRecursive3Method.invoke(service, n);
            int fourP = (int) solveFrameStewartMethod.invoke(service, n, 4);
            assertTrue(fourP <= threeP, "4-peg should require fewer or equal moves than 3-peg for " + n + " disks");
        }
    }

    @Test
    @DisplayName("Frame-Stewart known values: 3 disks = 5, 4 disks = 9, 5 disks = 13")
    void testFrameStewart_KnownValues() throws Exception {
        assertEquals(5, (int) solveFrameStewartMethod.invoke(service, 3, 4), "3 disks with 4 pegs = 5 moves");
        assertEquals(9, (int) solveFrameStewartMethod.invoke(service, 4, 4), "4 disks with 4 pegs = 9 moves");
        assertEquals(13, (int) solveFrameStewartMethod.invoke(service, 5, 4), "5 disks with 4 pegs = 13 moves");
    }

    // ==================== 4-Peg BFS Algorithm Tests ====================

    @Test
    @DisplayName("BFS 4-peg: 1 disk requires 1 move")
    void testBFS4_OneDisk() throws Exception {
        int result = (int) solveBFS4Method.invoke(service, 1);
        assertEquals(1, result, "1 disk requires 1 move");
    }

    @Test
    @DisplayName("BFS 4-peg: 2 disks require 3 moves")
    void testBFS4_TwoDisks() throws Exception {
        int result = (int) solveBFS4Method.invoke(service, 2);
        assertEquals(3, result, "2 disks require 3 moves");
    }

    @Test
    @DisplayName("BFS 4-peg: 3 disks require 5 moves")
    void testBFS4_ThreeDisks() throws Exception {
        int result = (int) solveBFS4Method.invoke(service, 3);
        assertEquals(5, result, "3 disks with 4 pegs require 5 moves");
    }

    // ==================== Frame-Stewart vs BFS Consistency ====================

    @Test
    @DisplayName("Frame-Stewart and BFS should return same result for small N")
    void testFrameStewart_BFS_Consistency() throws Exception {
        for (int n = 1; n <= 5; n++) {
            int frameStewart = (int) solveFrameStewartMethod.invoke(service, n, 4);
            int bfs = (int) solveBFS4Method.invoke(service, n);
            assertEquals(frameStewart, bfs, "Both algorithms should produce same result for " + n + " disks");
        }
    }
}
