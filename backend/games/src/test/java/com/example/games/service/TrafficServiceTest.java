package com.example.games.service;

import com.example.games.dto.TrafficDTOs.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrafficService
 * Tests Ford-Fulkerson and Edmonds-Karp algorithms for maximum flow
 * Note: Algorithms use hardcoded source "A" and sink "D"
 * Uses reflection to access private methods
 */
class TrafficServiceTest {

    private TrafficService service;
    private Method fordFulkersonMethod;
    private Method edmondsKarpMethod;

    @BeforeEach
    void setUp() throws Exception {
        service = new TrafficService();
        
        // Access private methods via reflection
        fordFulkersonMethod = TrafficService.class.getDeclaredMethod("fordFulkerson", List.class);
        fordFulkersonMethod.setAccessible(true);
        
        edmondsKarpMethod = TrafficService.class.getDeclaredMethod("edmondsKarp", List.class);
        edmondsKarpMethod.setAccessible(true);
    }

    // ==================== Ford-Fulkerson Algorithm Tests ====================

    @Test
    @DisplayName("Ford-Fulkerson: Algorithm executes without error")
    void testFordFulkerson_Executes() throws Exception {
        List<EdgeDTO> edges = createTestEdges();
        
        // Should not throw an exception
        int result = (int) fordFulkersonMethod.invoke(service, edges);
        assertTrue(result >= 0, "Max flow should be non-negative");
    }

    @Test
    @DisplayName("Ford-Fulkerson: Empty graph returns 0")
    void testFordFulkerson_EmptyGraph() throws Exception {
        List<EdgeDTO> edges = new ArrayList<>();
        
        int result = (int) fordFulkersonMethod.invoke(service, edges);
        assertEquals(0, result, "Empty graph should have 0 flow");
    }

    @Test
    @DisplayName("Ford-Fulkerson: Handles large capacity values")
    void testFordFulkerson_LargeCapacity() throws Exception {
        List<EdgeDTO> edges = Arrays.asList(
            new EdgeDTO("A", "B", 1000000),
            new EdgeDTO("B", "D", 1000000)
        );
        
        // Should not overflow or error
        int result = (int) fordFulkersonMethod.invoke(service, edges);
        assertTrue(result >= 0, "Should handle large capacities");
    }

    // ==================== Edmonds-Karp Algorithm Tests ====================

    @Test
    @DisplayName("Edmonds-Karp: Algorithm executes without error")
    void testEdmondsKarp_Executes() throws Exception {
        List<EdgeDTO> edges = createTestEdges();
        
        int result = (int) edmondsKarpMethod.invoke(service, edges);
        assertTrue(result >= 0, "Max flow should be non-negative");
    }

    @Test
    @DisplayName("Edmonds-Karp: Empty graph returns 0")
    void testEdmondsKarp_EmptyGraph() throws Exception {
        List<EdgeDTO> edges = new ArrayList<>();
        
        int result = (int) edmondsKarpMethod.invoke(service, edges);
        assertEquals(0, result, "Empty graph should have 0 flow");
    }

    @Test
    @DisplayName("Edmonds-Karp: Handles large capacity values")
    void testEdmondsKarp_LargeCapacity() throws Exception {
        List<EdgeDTO> edges = Arrays.asList(
            new EdgeDTO("A", "B", 1000000),
            new EdgeDTO("B", "D", 1000000)
        );
        
        int result = (int) edmondsKarpMethod.invoke(service, edges);
        assertTrue(result >= 0, "Should handle large capacities");
    }

    // ==================== Algorithm Consistency Tests ====================

    @Test
    @DisplayName("Ford-Fulkerson and Edmonds-Karp return same result")
    void testFF_EK_Consistency() throws Exception {
        List<EdgeDTO> edges = createTestEdges();
        
        int ffResult = (int) fordFulkersonMethod.invoke(service, edges);
        int ekResult = (int) edmondsKarpMethod.invoke(service, edges);
        
        assertEquals(ffResult, ekResult, "Both algorithms should return same max flow");
    }

    @Test
    @DisplayName("Multiple runs are deterministic")
    void testDeterministic() throws Exception {
        List<EdgeDTO> edges = createTestEdges();
        
        int result1 = (int) fordFulkersonMethod.invoke(service, edges);
        int result2 = (int) fordFulkersonMethod.invoke(service, edges);
        
        assertEquals(result1, result2, "Algorithm should be deterministic");
    }

    @Test
    @DisplayName("Algorithms handle disconnected graph gracefully")
    void testDisconnectedGraph() throws Exception {
        List<EdgeDTO> edges = Arrays.asList(
            new EdgeDTO("A", "B", 10),
            new EdgeDTO("C", "D", 10)  // No path from A to D
        );
        
        int ffResult = (int) fordFulkersonMethod.invoke(service, edges);
        int ekResult = (int) edmondsKarpMethod.invoke(service, edges);
        
        assertEquals(ffResult, ekResult, "Both should handle disconnected graph same way");
    }

    // ==================== Helper Methods ====================

    private List<EdgeDTO> createTestEdges() {
        return Arrays.asList(
            new EdgeDTO("A", "B", 10),
            new EdgeDTO("A", "C", 8),
            new EdgeDTO("B", "C", 5),
            new EdgeDTO("B", "D", 7),
            new EdgeDTO("C", "D", 10)
        );
    }
}
