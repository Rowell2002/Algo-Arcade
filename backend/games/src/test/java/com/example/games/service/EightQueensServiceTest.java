package com.example.games.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EightQueensService
 * Tests Sequential and Threaded backtracking algorithms for 8-Queens puzzle
 * Uses reflection for all private methods
 */
class EightQueensServiceTest {

    private EightQueensService service;
    private Method solveSequentialMethod;
    private Method solveThreadedMethod;
    private Method isSafeMethod;
    private Method isValidPlacementMethod;
    private Method toCanonicalStringMethod;

    @BeforeEach
    void setUp() throws Exception {
        service = new EightQueensService();
        
        // Access private methods via reflection
        solveSequentialMethod = EightQueensService.class.getDeclaredMethod("solveSequential");
        solveSequentialMethod.setAccessible(true);
        
        solveThreadedMethod = EightQueensService.class.getDeclaredMethod("solveThreaded");
        solveThreadedMethod.setAccessible(true);
        
        isSafeMethod = EightQueensService.class.getDeclaredMethod("isSafe", int.class, int.class, int[].class);
        isSafeMethod.setAccessible(true);
        
        isValidPlacementMethod = EightQueensService.class.getDeclaredMethod("isValidPlacement", List.class);
        isValidPlacementMethod.setAccessible(true);
        
        toCanonicalStringMethod = EightQueensService.class.getDeclaredMethod("toCanonicalString", List.class);
        toCanonicalStringMethod.setAccessible(true);
    }

    // ==================== Sequential Backtracking Tests ====================

    @Test
    @DisplayName("Sequential: Should find exactly 92 solutions")
    @SuppressWarnings("unchecked")
    void testSequential_FindsAllSolutions() throws Exception {
        List<List<Integer>> solutions = (List<List<Integer>>) solveSequentialMethod.invoke(service);
        
        assertEquals(92, solutions.size(), "There are exactly 92 solutions to 8-Queens");
    }

    @Test
    @DisplayName("Sequential: All solutions should be valid")
    @SuppressWarnings("unchecked")
    void testSequential_AllSolutionsValid() throws Exception {
        List<List<Integer>> solutions = (List<List<Integer>>) solveSequentialMethod.invoke(service);
        
        for (List<Integer> solution : solutions) {
            boolean isValid = (boolean) isValidPlacementMethod.invoke(service, solution);
            assertTrue(isValid, "Each solution should be a valid 8-Queens placement");
        }
    }

    @Test
    @DisplayName("Sequential: Each solution should have 8 queens")
    @SuppressWarnings("unchecked")
    void testSequential_SolutionSize() throws Exception {
        List<List<Integer>> solutions = (List<List<Integer>>) solveSequentialMethod.invoke(service);
        
        for (List<Integer> solution : solutions) {
            assertEquals(8, solution.size(), "Each solution should have 8 queens");
        }
    }

    @Test
    @DisplayName("Sequential: All solutions should be unique")
    @SuppressWarnings("unchecked")
    void testSequential_AllSolutionsUnique() throws Exception {
        List<List<Integer>> solutions = (List<List<Integer>>) solveSequentialMethod.invoke(service);
        Set<String> uniqueSolutions = new HashSet<>();
        
        for (List<Integer> solution : solutions) {
            String key = solution.toString();
            uniqueSolutions.add(key);
        }
        
        assertEquals(92, uniqueSolutions.size(), "All 92 solutions should be unique");
    }

    // ==================== Threaded Backtracking Tests ====================

    @Test
    @DisplayName("Threaded: Should find exactly 92 solutions")
    @SuppressWarnings("unchecked")
    void testThreaded_FindsAllSolutions() throws Exception {
        List<List<Integer>> solutions = (List<List<Integer>>) solveThreadedMethod.invoke(service);
        
        assertEquals(92, solutions.size(), "There are exactly 92 solutions to 8-Queens");
    }

    @Test
    @DisplayName("Threaded: All solutions should be valid")
    @SuppressWarnings("unchecked")
    void testThreaded_AllSolutionsValid() throws Exception {
        List<List<Integer>> solutions = (List<List<Integer>>) solveThreadedMethod.invoke(service);
        
        for (List<Integer> solution : solutions) {
            boolean isValid = (boolean) isValidPlacementMethod.invoke(service, solution);
            assertTrue(isValid, "Each solution should be a valid 8-Queens placement");
        }
    }

    @Test
    @DisplayName("Threaded: All solutions should be unique")
    @SuppressWarnings("unchecked")
    void testThreaded_AllSolutionsUnique() throws Exception {
        List<List<Integer>> solutions = (List<List<Integer>>) solveThreadedMethod.invoke(service);
        Set<String> uniqueSolutions = new HashSet<>();
        
        for (List<Integer> solution : solutions) {
            String key = solution.toString();
            uniqueSolutions.add(key);
        }
        
        assertEquals(92, uniqueSolutions.size(), "All 92 solutions should be unique");
    }

    // ==================== Sequential vs Threaded Consistency ====================

    @Test
    @DisplayName("Sequential and Threaded should find same solutions")
    @SuppressWarnings("unchecked")
    void testSequential_Threaded_Consistency() throws Exception {
        List<List<Integer>> seqSolutions = (List<List<Integer>>) solveSequentialMethod.invoke(service);
        List<List<Integer>> threadSolutions = (List<List<Integer>>) solveThreadedMethod.invoke(service);
        
        assertEquals(seqSolutions.size(), threadSolutions.size(), 
            "Both algorithms should find same number of solutions");
        
        Set<String> seqSet = new HashSet<>();
        Set<String> threadSet = new HashSet<>();
        
        for (List<Integer> sol : seqSolutions) seqSet.add(sol.toString());
        for (List<Integer> sol : threadSolutions) threadSet.add(sol.toString());
        
        assertEquals(seqSet, threadSet, "Both algorithms should find same solutions");
    }

    // ==================== isValidPlacement Tests ====================

    @Test
    @DisplayName("Valid placement: Known correct solution")
    void testIsValidPlacement_ValidSolution() throws Exception {
        List<Integer> valid = Arrays.asList(0, 4, 7, 5, 2, 6, 1, 3);
        boolean isValid = (boolean) isValidPlacementMethod.invoke(service, valid);
        assertTrue(isValid, "Known valid solution should be valid");
    }

    @Test
    @DisplayName("Invalid placement: Queens in same column")
    void testIsValidPlacement_SameColumn() throws Exception {
        List<Integer> invalid = Arrays.asList(0, 0, 7, 5, 2, 6, 1, 3);
        boolean isValid = (boolean) isValidPlacementMethod.invoke(service, invalid);
        assertFalse(isValid, "Queens in same column should be invalid");
    }

    @Test
    @DisplayName("Invalid placement: Queens on diagonal")
    void testIsValidPlacement_Diagonal() throws Exception {
        List<Integer> invalid = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
        boolean isValid = (boolean) isValidPlacementMethod.invoke(service, invalid);
        assertFalse(isValid, "Queens on diagonal should be invalid");
    }

    @Test
    @DisplayName("Invalid placement: Wrong size")
    void testIsValidPlacement_WrongSize() throws Exception {
        List<Integer> invalid = Arrays.asList(0, 4, 7);
        boolean isValid = (boolean) isValidPlacementMethod.invoke(service, invalid);
        assertFalse(isValid, "Solution with wrong size should be invalid");
    }

    @Test
    @DisplayName("Invalid placement: Null input")
    void testIsValidPlacement_Null() throws Exception {
        boolean isValid = (boolean) isValidPlacementMethod.invoke(service, (List<Integer>) null);
        assertFalse(isValid, "Null input should be invalid");
    }

    // ==================== toCanonicalString Tests ====================

    @Test
    @DisplayName("Canonical string: Correct format")
    void testToCanonicalString_Format() throws Exception {
        List<Integer> solution = Arrays.asList(0, 4, 7, 5, 2, 6, 1, 3);
        String canonical = (String) toCanonicalStringMethod.invoke(service, solution);
        
        assertEquals("04752613", canonical, "Canonical string should be digits concatenated");
    }

    @Test
    @DisplayName("Canonical string: Different solutions have different strings")
    void testToCanonicalString_Unique() throws Exception {
        List<Integer> sol1 = Arrays.asList(0, 4, 7, 5, 2, 6, 1, 3);
        List<Integer> sol2 = Arrays.asList(0, 5, 7, 2, 6, 3, 1, 4);
        
        String str1 = (String) toCanonicalStringMethod.invoke(service, sol1);
        String str2 = (String) toCanonicalStringMethod.invoke(service, sol2);
        
        assertNotEquals(str1, str2, "Different solutions should have different canonical strings");
    }

    // ==================== isSafe Tests ====================

    @Test
    @DisplayName("isSafe: Empty board is always safe")
    void testIsSafe_EmptyBoard() throws Exception {
        int[] queens = new int[8];
        Arrays.fill(queens, -1);
        
        for (int col = 0; col < 8; col++) {
            boolean result = (boolean) isSafeMethod.invoke(service, 0, col, queens);
            assertTrue(result, "Any column on empty board should be safe");
        }
    }

    @Test
    @DisplayName("isSafe: Same column is not safe")
    void testIsSafe_SameColumn() throws Exception {
        int[] queens = new int[8];
        Arrays.fill(queens, -1);
        queens[0] = 3;
        
        boolean result = (boolean) isSafeMethod.invoke(service, 1, 3, queens);
        assertFalse(result, "Same column should not be safe");
    }

    @Test
    @DisplayName("isSafe: Diagonal is not safe")
    void testIsSafe_Diagonal() throws Exception {
        int[] queens = new int[8];
        Arrays.fill(queens, -1);
        queens[0] = 3;
        
        boolean left = (boolean) isSafeMethod.invoke(service, 1, 2, queens);
        boolean right = (boolean) isSafeMethod.invoke(service, 1, 4, queens);
        
        assertFalse(left, "Left diagonal should not be safe");
        assertFalse(right, "Right diagonal should not be safe");
    }

    // ==================== Performance Tests ====================

    @Test
    @DisplayName("Sequential algorithm completes in reasonable time")
    void testSequential_Performance() throws Exception {
        long start = System.currentTimeMillis();
        solveSequentialMethod.invoke(service);
        long duration = System.currentTimeMillis() - start;
        
        assertTrue(duration < 5000, "Sequential should complete within 5 seconds");
    }

    @Test
    @DisplayName("Threaded algorithm completes in reasonable time")
    void testThreaded_Performance() throws Exception {
        long start = System.currentTimeMillis();
        solveThreadedMethod.invoke(service);
        long duration = System.currentTimeMillis() - start;
        
        assertTrue(duration < 5000, "Threaded should complete within 5 seconds");
    }
}
