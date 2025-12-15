package com.example.games.service;

import com.example.games.dto.TspDTOs.TspGameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TspService
 * Tests Brute Force, Nearest Neighbor, and Dynamic Programming algorithms
 * Uses reflection to access private methods
 */
class TspServiceTest {

    private TspService service;
    private Method solveBruteForceMethod;
    private Method solveNearestNeighborMethod;
    private Method solveDPMethod;
    private Method getDistanceMethod;

    @BeforeEach
    void setUp() throws Exception {
        service = new TspService();
        
        // Access private methods via reflection
        solveBruteForceMethod = TspService.class.getDeclaredMethod("solveBruteForce", TspGameData.class, String.class, List.class);
        solveBruteForceMethod.setAccessible(true);
        
        solveNearestNeighborMethod = TspService.class.getDeclaredMethod("solveNearestNeighbor", TspGameData.class, String.class, List.class);
        solveNearestNeighborMethod.setAccessible(true);
        
        solveDPMethod = TspService.class.getDeclaredMethod("solveDP", TspGameData.class, String.class, List.class);
        solveDPMethod.setAccessible(true);
        
        getDistanceMethod = TspService.class.getDeclaredMethod("getDistance", TspGameData.class, String.class, String.class);
        getDistanceMethod.setAccessible(true);
    }

    // ==================== Brute Force Algorithm Tests ====================

    @Test
    @DisplayName("Brute Force: Simple 3-city tour returns valid result")
    void testBruteForce_ThreeCities() throws Exception {
        TspGameData data = createMockGameData();
        List<String> cities = Arrays.asList("B", "C");
        
        Object result = solveBruteForceMethod.invoke(service, data, "A", cities);
        
        assertNotNull(result, "Should return a result");
    }

    @Test
    @DisplayName("Brute Force: Single city tour")
    void testBruteForce_SingleCity() throws Exception {
        TspGameData data = createMockGameData();
        List<String> cities = Arrays.asList("B");
        
        Object result = solveBruteForceMethod.invoke(service, data, "A", cities);
        
        assertNotNull(result, "Should return a result for single city");
    }

    @Test
    @DisplayName("Brute Force: Four cities tour")
    void testBruteForce_FourCities() throws Exception {
        TspGameData data = createMockGameData();
        List<String> cities = Arrays.asList("B", "C", "D");
        
        Object result = solveBruteForceMethod.invoke(service, data, "A", cities);
        
        assertNotNull(result, "Should return a result for 4 cities");
    }

    // ==================== Nearest Neighbor Algorithm Tests ====================

    @Test
    @DisplayName("Nearest Neighbor: Simple 3-city tour returns valid result")
    void testNearestNeighbor_ThreeCities() throws Exception {
        TspGameData data = createMockGameData();
        List<String> cities = Arrays.asList("B", "C");
        
        Object result = solveNearestNeighborMethod.invoke(service, data, "A", cities);
        
        assertNotNull(result, "Should return a result");
    }

    @Test
    @DisplayName("Nearest Neighbor: Single city tour")
    void testNearestNeighbor_SingleCity() throws Exception {
        TspGameData data = createMockGameData();
        List<String> cities = Arrays.asList("B");
        
        Object result = solveNearestNeighborMethod.invoke(service, data, "A", cities);
        
        assertNotNull(result, "Should return a result for single city");
    }

    @Test
    @DisplayName("Nearest Neighbor: Four cities tour")
    void testNearestNeighbor_FourCities() throws Exception {
        TspGameData data = createMockGameData();
        List<String> cities = Arrays.asList("B", "C", "D");
        
        Object result = solveNearestNeighborMethod.invoke(service, data, "A", cities);
        
        assertNotNull(result, "Should return a result for 4 cities");
    }

    // ==================== Dynamic Programming Tests ====================

    @Test
    @DisplayName("DP: Simple 3-city tour returns valid result")
    void testDP_ThreeCities() throws Exception {
        TspGameData data = createMockGameData();
        List<String> cities = Arrays.asList("B", "C");
        
        Object result = solveDPMethod.invoke(service, data, "A", cities);
        
        assertNotNull(result, "Should return a result");
    }

    @Test
    @DisplayName("DP: Four cities tour")
    void testDP_FourCities() throws Exception {
        TspGameData data = createMockGameData();
        List<String> cities = Arrays.asList("B", "C", "D");
        
        Object result = solveDPMethod.invoke(service, data, "A", cities);
        
        assertNotNull(result, "Should return a result");
    }

    // ==================== getDistance Tests ====================

    @Test
    @DisplayName("getDistance: Returns correct distance between cities")
    void testGetDistance() throws Exception {
        TspGameData data = createMockGameData();
        
        int dist = (int) getDistanceMethod.invoke(service, data, "A", "B");
        
        assertEquals(10, dist, "Distance A->B should be 10");
    }

    @Test
    @DisplayName("getDistance: Same city returns 0")
    void testGetDistance_SameCity() throws Exception {
        TspGameData data = createMockGameData();
        
        int dist = (int) getDistanceMethod.invoke(service, data, "A", "A");
        
        assertEquals(0, dist, "Distance from city to itself should be 0");
    }

    @Test
    @DisplayName("getDistance: Symmetric distance")
    void testGetDistance_Symmetric() throws Exception {
        TspGameData data = createMockGameData();
        
        int distAB = (int) getDistanceMethod.invoke(service, data, "A", "B");
        int distBA = (int) getDistanceMethod.invoke(service, data, "B", "A");
        
        assertEquals(distAB, distBA, "Distance should be symmetric");
    }

    // ==================== Helper Methods ====================

    private TspGameData createMockGameData() {
        TspGameData data = new TspGameData();
        data.setGameId(UUID.randomUUID().toString());
        data.setPlayerName("TestPlayer");
        data.setHomeCity("A");
        
        // Create mock distances (no need for CityDTO)
        Map<String, Map<String, Integer>> distances = new HashMap<>();
        String[] cityNames = {"A", "B", "C", "D", "E"};
        int[][] distMatrix = {
            {0, 10, 10, 14, 7},
            {10, 0, 14, 10, 7},
            {10, 14, 0, 10, 7},
            {14, 10, 10, 0, 7},
            {7, 7, 7, 7, 0}
        };
        
        for (int i = 0; i < cityNames.length; i++) {
            distances.put(cityNames[i], new HashMap<>());
            for (int j = 0; j < cityNames.length; j++) {
                distances.get(cityNames[i]).put(cityNames[j], distMatrix[i][j]);
            }
        }
        data.setDistances(distances);
        
        return data;
    }
}
