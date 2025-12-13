package com.example.games.service;

import com.example.games.dto.TrafficDTOs.*;
import com.example.games.entity.TrafficGameResult;
import com.example.games.repository.TrafficRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TrafficService {

    @Autowired
    private TrafficRepository repository;

    private final Map<String, TrafficGameData> activeGames = new ConcurrentHashMap<>();

    // Graph Structure: String Node -> Map<String Neighbor, Integer Capacity>
    // Simplified for fixed nodes: A, B, C, D, E, F, G, H, T
    // Using simple adjacency constant structure for key definition
    private static final String SRC = "A";
    private static final String SINK = "T";

    public TrafficGameData startGame(String playerName) {
        String gameId = UUID.randomUUID().toString();
        
        List<EdgeDTO> edges = new ArrayList<>();
        Random rand = new Random();

        // Edge definitions as per requirements (Node -> Node)
        // A->B, A->C, A->D
        // B->E, B->F
        // C->E, C->F
        // D->F
        // E->G, E->H
        // F->H
        // G->T
        // H->T

        addRandomEdge(edges, "A", "B", rand);
        addRandomEdge(edges, "A", "C", rand);
        addRandomEdge(edges, "A", "D", rand);
        addRandomEdge(edges, "B", "E", rand);
        addRandomEdge(edges, "B", "F", rand);
        addRandomEdge(edges, "C", "E", rand);
        addRandomEdge(edges, "C", "F", rand);
        addRandomEdge(edges, "D", "F", rand);
        addRandomEdge(edges, "E", "G", rand);
        addRandomEdge(edges, "E", "H", rand);
        addRandomEdge(edges, "F", "H", rand);
        addRandomEdge(edges, "G", "T", rand);
        addRandomEdge(edges, "H", "T", rand);

        TrafficGameData data = new TrafficGameData();
        data.setGameId(gameId);
        data.setPlayerName(playerName);
        data.setEdges(edges);

        activeGames.put(gameId, data);
        return data;
    }

    private void addRandomEdge(List<EdgeDTO> edges, String u, String v, Random rand) {
        // Random capacity 5-15
        int cap = rand.nextInt(11) + 5; 
        edges.add(new EdgeDTO(u, v, cap));
    }

    public TrafficResultResponse solveGame(String gameId, int userGuess) {
        TrafficGameData game = activeGames.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        Map<String, Long> times = new HashMap<>();

        // 1. Ford-Fulkerson (DFS)
        long startFF = System.nanoTime();
        int maxFlowFF = fordFulkerson(game.getEdges());
        long endFF = System.nanoTime();
        times.put("FordFulkerson", (endFF - startFF) / 1000);

        // 2. Edmonds-Karp (BFS)
        long startEK = System.nanoTime();
        int maxFlowEK = edmondsKarp(game.getEdges());
        long endEK = System.nanoTime();
        times.put("EdmondsKarp", (endEK - startEK) / 1000);

        // Save result
        TrafficGameResult result = new TrafficGameResult();
        // Only save player name if answer is correct
        result.setPlayerName(userGuess == maxFlowFF ? game.getPlayerName() : null);
        result.setMaxFlow(maxFlowFF); // Assign one correct value
        result.setUserGuess(userGuess);
        result.setFordFulkersonTime((endFF - startFF) / 1000);
        result.setEdmondsKarpTime((endEK - startEK) / 1000);
        
        repository.save(result);

        TrafficResultResponse response = new TrafficResultResponse();
        response.setMaxFlow(maxFlowFF);
        response.setUserGuess(userGuess);
        response.setCorrect(userGuess == maxFlowFF);
        response.setAlgorithmTimes(times);
        response.setMessage(response.isCorrect() ? "Correct! Optimal flow found." : "Incorrect. Max flow is " + maxFlowFF);
        
        return response;
    }

    // --- Graph Helpers ---
    private Map<String, Map<String, Integer>> buildGraph(List<EdgeDTO> edges) {
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        for (EdgeDTO e : edges) {
            graph.computeIfAbsent(e.getFrom(), k -> new HashMap<>()).put(e.getTo(), e.getCapacity());
            // Ensure nodes exist in map even if no outgoing edges (for sink)
            graph.computeIfAbsent(e.getTo(), k -> new HashMap<>());
        }
        return graph;
    }
    
    // Deep copy for isolation between algos
    private Map<String, Map<String, Integer>> cloneGraph(Map<String, Map<String, Integer>> original) {
        Map<String, Map<String, Integer>> copy = new HashMap<>();
        for (String u : original.keySet()) {
            copy.put(u, new HashMap<>(original.get(u)));
        }
        return copy;
    }

    // --- ALGORITHMS ---

    // 1. Ford-Fulkerson using DFS
    private int fordFulkerson(List<EdgeDTO> edges) {
        // Build Residual Graph. In simplified implementation, residual edges (backward) start with 0 capacity.
        // Or we manage flows explicitly.
        // Standard way: Map<u, Map<v, capacity>> where capacity includes residual.
        
        Map<String, Map<String, Integer>> capacity = new HashMap<>();
        for(EdgeDTO e : edges) {
            capacity.computeIfAbsent(e.getFrom(), k->new HashMap<>()).put(e.getTo(), e.getCapacity());
            capacity.computeIfAbsent(e.getTo(), k->new HashMap<>()).put(e.getFrom(), 0); // Backward edge init 0
        }

        int maxFlow = 0;
        
        while (true) {
            Map<String, String> parent = new HashMap<>();
            // Use DFS to find path
            Set<String> visited = new HashSet<>();
            if (!dfs(SRC, SINK, visited, capacity, parent)) {
                break;
            }

            // Find bottle neck
            int pathFlow = Integer.MAX_VALUE;
            String v = SINK;
            while (!v.equals(SRC)) {
                String u = parent.get(v);
                pathFlow = Math.min(pathFlow, capacity.get(u).get(v));
                v = u;
            }

            // Update residual capacities
            v = SINK;
            while (!v.equals(SRC)) {
                String u = parent.get(v);
                capacity.get(u).put(v, capacity.get(u).get(v) - pathFlow);
                capacity.get(v).put(u, capacity.get(v).get(u) + pathFlow);
                v = u;
            }
            maxFlow += pathFlow;
        }
        return maxFlow;
    }

    private boolean dfs(String u, String t, Set<String> visited, Map<String, Map<String, Integer>> capacity, Map<String, String> parent) {
        visited.add(u);
        if (u.equals(t)) return true;

        if (capacity.containsKey(u)) {
            for (String v : capacity.get(u).keySet()) {
                if (!visited.contains(v) && capacity.get(u).get(v) > 0) {
                    parent.put(v, u);
                    if (dfs(v, t, visited, capacity, parent)) return true;
                }
            }
        }
        return false;
    }


    // 2. Edmonds-Karp using BFS
    private int edmondsKarp(List<EdgeDTO> edges) {
        Map<String, Map<String, Integer>> capacity = new HashMap<>();
        for(EdgeDTO e : edges) {
            capacity.computeIfAbsent(e.getFrom(), k->new HashMap<>()).put(e.getTo(), e.getCapacity());
            capacity.computeIfAbsent(e.getTo(), k->new HashMap<>()).put(e.getFrom(), 0); // Backward edge init 0
        }

        int maxFlow = 0;

        while (true) {
            Map<String, String> parent = new HashMap<>();
            Queue<String> queue = new LinkedList<>();
            queue.add(SRC);
            parent.put(SRC, null);
            
            boolean pathFound = false;
            while (!queue.isEmpty()) {
                String u = queue.poll();
                if (u.equals(SINK)) {
                    pathFound = true;
                    break;
                }
                
                if (capacity.containsKey(u)) {
                    for (String v : capacity.get(u).keySet()) {
                        if (!parent.containsKey(v) && capacity.get(u).get(v) > 0) {
                            parent.put(v, u);
                            queue.add(v);
                        }
                    }
                }
            }

            if (!pathFound) break;

            // Find flow
            int pathFlow = Integer.MAX_VALUE;
            String v = SINK;
            while (!v.equals(SRC)) {
                String u = parent.get(v);
                pathFlow = Math.min(pathFlow, capacity.get(u).get(v));
                v = u;
            }

            // Update
            v = SINK;
            while (!v.equals(SRC)) {
                String u = parent.get(v);
                capacity.get(u).put(v, capacity.get(u).get(v) - pathFlow);
                capacity.get(v).put(u, capacity.get(v).get(u) + pathFlow);
                v = u;
            }
            maxFlow += pathFlow;
        }

        return maxFlow;
    }
    
    public Map<String, Object> getComparisonData() {
        List<TrafficGameResult> games = repository.findTop15ByOrderByCreatedAtDesc();
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> gamesList = new ArrayList<>();
        
        long ffSum = 0;
        long ekSum = 0;
        int count = games.size();
        
        for (TrafficGameResult game : games) {
            Map<String, Object> gameData = new HashMap<>();
            gameData.put("gameId", game.getId());
            gameData.put("playerName", game.getPlayerName());
            gameData.put("createdAt", game.getCreatedAt());
            
            Map<String, Long> algorithmTimes = new HashMap<>();
            algorithmTimes.put("FordFulkerson", game.getFordFulkersonTime());
            algorithmTimes.put("EdmondsKarp", game.getEdmondsKarpTime());
            gameData.put("algorithmTimes", algorithmTimes);
            
            gamesList.add(gameData);
            ffSum += game.getFordFulkersonTime();
            ekSum += game.getEdmondsKarpTime();
        }
        
        Map<String, Long> averages = new HashMap<>();
        if (count > 0) {
            averages.put("FordFulkerson", ffSum / count);
            averages.put("EdmondsKarp", ekSum / count);
        }
        
        result.put("games", gamesList);
        result.put("averages", averages);
        result.put("count", count);
        
        return result;
    }
}
