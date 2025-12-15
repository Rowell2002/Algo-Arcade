package com.example.games.service;

import com.example.games.dto.TspDTOs.*;
import com.example.games.entity.TspGameResult;
import com.example.games.repository.TspRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TspService {

    @Autowired
    private TspRepository repository;

    // In-memory store for active game data (simplified for this project)
    private Map<String, TspGameData> activeGames = new ConcurrentHashMap<>();

    private static final String[] CITIES = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    public TspGameData startGame(StartGameRequest request) {
        String gameId = UUID.randomUUID().toString();
        TspGameData data = new TspGameData();
        data.setGameId(gameId);
        data.setPlayerName(request.getPlayerName());

        // Random Home City
        String homeCity = CITIES[new Random().nextInt(CITIES.length)];
        data.setHomeCity(homeCity);

        // Generate Random Distances (Undirected, Complete Graph)
        // 50 to 100 km
        Map<String, Map<String, Integer>> distances = new HashMap<>();
        Random random = new Random();

        for (String c1 : CITIES) {
            distances.putIfAbsent(c1, new HashMap<>());
            for (String c2 : CITIES) {
                if (c1.equals(c2)) {
                    distances.get(c1).put(c2, 0);
                } else {
                    int dist;
                    if (distances.containsKey(c2) && distances.get(c2).containsKey(c1)) {
                        dist = distances.get(c2).get(c1);
                    } else {
                        dist = 50 + random.nextInt(51); // 50-100 inclusive
                    }
                    distances.get(c1).put(c2, dist);
                }
            }
        }
        data.setDistances(distances);
        activeGames.put(gameId, data);
        return data;
    }

    public TspResult solveGame(String gameId, SolveTspRequest request) {
        TspGameData gameData = activeGames.get(gameId);
        if (gameData == null) {
            throw new RuntimeException("Game not found");
        }

        // Cities to visit (User Selection) + Home
        // Request contains visited cities in order.
        // We need to identify the set of cities user chose to visit.
        // And assume user implicitly starts at Home and ends at Home.
        
        List<String> visitedOrder = request.getVisitedCities();
        if (visitedOrder == null || visitedOrder.isEmpty()) {
             throw new RuntimeException("No path provided");
        }
        
        String home = gameData.getHomeCity();
        Set<String> nodesToVisit = new HashSet<>(visitedOrder);
        // Exclude home from set if present (it shouldn't be in the middle usually? logic depends on input)
        // Standard TSP: Start Home -> [Set of N Cities] -> End Home.
        // User Input: Ordered list of N cities.
        
        // Validation:
        // 1. Path must not include duplicates? (Requirement: visit exactly once)
        if (new HashSet<>(visitedOrder).size() != visitedOrder.size()) {
             TspResult res = new TspResult();
             res.setCorrect(false);
             res.setMessage("Invalid path: Cities must be visited exactly once.");
             return res;
        }

        // Calculate User Distance
        int userDist = 0;
        String current = home;
        for (String next : visitedOrder) {
            userDist += getDistance(gameData, current, next);
            current = next;
        }
        userDist += getDistance(gameData, current, home); // Return execution

        // --- Run Algorithms ---
        
        // Nodes to involve: Home + nodesToVisit
        List<String> problemNodes = new ArrayList<>(visitedOrder); 
        // Note: The optimal order might be different from user order.
        // We need to find optimal permutation of 'problemNodes'.
        
        // 1. Brute Force
        long startBF = System.nanoTime();
        PathResult bfResult = solveBruteForce(gameData, home, problemNodes);
        long endBF = System.nanoTime();

        // 2. Nearest Neighbor
        long startNN = System.nanoTime();
        PathResult nnResult = solveNearestNeighbor(gameData, home, problemNodes);
        long endNN = System.nanoTime();

        // 3. Dynamic Programming (Held-Karp)
        long startDP = System.nanoTime();
        PathResult dpResult = solveDP(gameData, home, problemNodes);
        long endDP = System.nanoTime();
        
        // Prepare Result
        TspResult result = new TspResult();
        result.setUserDistance(userDist);
        result.setMinDistance(bfResult.distance); // BF is exact
        result.setOptimalPath(bfResult.path); // Full path: Home -> ... -> Home
        
        // Is Correct?
        boolean isCorrect = userDist == bfResult.distance;
        result.setCorrect(isCorrect);
        result.setMessage(isCorrect 
            ? "Correct! You found the shortest path." 
            : "Incorrect. optimal was " + bfResult.distance + "km vs yours " + userDist + "km.");
        
        // Save to DB - always save, but only include player name if correct
        TspGameResult entity = new TspGameResult();
        entity.setPlayerName(isCorrect ? gameData.getPlayerName() : null);
        entity.setHomeCity(home);
        entity.setSelectedCities(String.join(",", visitedOrder));
        entity.setOptimalPath(String.join("->", bfResult.path));
        entity.setMinDistance(bfResult.distance);
        entity.setUserDistance(userDist);
        
        entity.setBruteForceTimeNs(endBF - startBF);
        entity.setNearestNeighborTimeNs(endNN - startNN);
        entity.setDynamicProgrammingTimeNs(endDP - startDP);
        
        repository.save(entity);
        
        Map<String, Long> times = new HashMap<>();
        times.put("Brute Force", endBF - startBF);
        times.put("Nearest Neighbor", endNN - startNN);
        times.put("Dynamic Programming", endDP - startDP);
        result.setAlgorithmTimes(times);
        
        Map<String, String> complexities = new HashMap<>();
        complexities.put("Brute Force", "O(N!)");
        complexities.put("Nearest Neighbor", "O(N^2)");
        complexities.put("Dynamic Programming", "O(N^2 * 2^N)");
        result.setAlgorithmComplexities(complexities);

        return result;
    }

    private int getDistance(TspGameData data, String c1, String c2) {
        return data.getDistances().get(c1).get(c2);
    }
    
    // --- Solvers ---
    
    // Helpers
    private static class PathResult {
        List<String> path; // Includes start and end
        int distance;
        public PathResult(List<String> path, int distance) { this.path = path; this.distance = distance; }
    }

    // 1. Brute Force (Permutations)
    private PathResult solveBruteForce(TspGameData data, String home, List<String> citiesToVisit) {
        List<List<String>> perms = generatePermutations(citiesToVisit);
        int minCost = Integer.MAX_VALUE;
        List<String> bestPath = null;
        
        for (List<String> p : perms) {
            int currentCost = 0;
            String current = home;
            
            List<String> currentPathFull = new ArrayList<>();
            currentPathFull.add(home);
            
            for (String next : p) {
                currentCost += getDistance(data, current, next);
                currentPathFull.add(next);
                current = next;
            }
            currentCost += getDistance(data, current, home);
            currentPathFull.add(home);
            
            if (currentCost < minCost) {
                minCost = currentCost;
                bestPath = new ArrayList<>(currentPathFull);
            }
        }
        return new PathResult(bestPath, minCost);
    }

    private List<List<String>> generatePermutations(List<String> original) {
        if (original.isEmpty()) {
            List<List<String>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }
        String first = original.get(0);
        List<List<String>> rest = generatePermutations(original.subList(1, original.size()));
        List<List<String>> all = new ArrayList<>();
        
        for (List<String> p : rest) {
            for (int i = 0; i <= p.size(); i++) {
                List<String> temp = new ArrayList<>(p);
                temp.add(i, first);
                all.add(temp);
            }
        }
        return all;
    }

    // 2. Nearest Neighbor
    private PathResult solveNearestNeighbor(TspGameData data, String home, List<String> citiesToVisit) {
        List<String> unvisited = new ArrayList<>(citiesToVisit);
        List<String> path = new ArrayList<>();
        path.add(home);
        
        String current = home;
        int totalDist = 0;
        
        while (!unvisited.isEmpty()) {
            String nearest = null;
            int minDist = Integer.MAX_VALUE;
            
            for (String cand : unvisited) {
                int d = getDistance(data, current, cand);
                if (d < minDist) {
                    minDist = d;
                    nearest = cand;
                }
            }
            
            totalDist += minDist;
            current = nearest;
            path.add(current);
            unvisited.remove(current);
        }
        
        // Return to home
        totalDist += getDistance(data, current, home);
        path.add(home);
        
        return new PathResult(path, totalDist);
    }

    // 3. DP (Held-Karp) - Simplified for robustness since N is small
    // Map<Mask, Map<LastCity, MinCost>>
    private PathResult solveDP(TspGameData data, String home, List<String> citiesToVisit) {
        // Need mapping string -> int index
        List<String> allNodes = new ArrayList<>();
        allNodes.add(home);
        allNodes.addAll(citiesToVisit);
        int n = allNodes.size();
        
        // distances[i][j]
        int[][] dist = new int[n][n];
        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                dist[i][j] = getDistance(data, allNodes.get(i), allNodes.get(j));
            }
        }
        
        // dp[mask][last] = min cost to visit set 'mask' ending at city 'last'
        // mask is bitmask of visited cities. Home is index 0.
        int[][] dp = new int[1 << n][n];
        for (int[] row : dp) Arrays.fill(row, Integer.MAX_VALUE / 2);
        
        dp[1][0] = 0; // Visited {Home}, ending at Home. Cost 0.
        
        for (int mask = 1; mask < (1 << n); mask += 2) { // mask always includes bit 0 (Home)
            for (int prev = 0; prev < n; prev++) {
                if ((mask & (1 << prev)) != 0 && dp[mask][prev] < Integer.MAX_VALUE / 2) {
                    // Try to go to 'next'
                    for (int next = 1; next < n; next++) { // next != 0
                        if ((mask & (1 << next)) == 0) {
                            int newMask = mask | (1 << next);
                            dp[newMask][next] = Math.min(dp[newMask][next], dp[mask][prev] + dist[prev][next]);
                        }
                    }
                }
            }
        }
        
        // Final step: return to home from all possible last nodes in full mask
        int fullMask = (1 << n) - 1;
        int minCost = Integer.MAX_VALUE;
        // Reconstruct path excluded for brevity, just getting cost? 
        // Or if we need path, we need parent pointers. 
        // For game purposes, BruteForce path is sufficient for display. 
        // DP is mostly for timing comparison.
        
        for (int i = 1; i < n; i++) {
            minCost = Math.min(minCost, dp[fullMask][i] + dist[i][0]);
        }
        
        // Return Empty path with correct cost, since we rely on BF for exact path display
        return new PathResult(new ArrayList<>(), minCost);
    }
    
    public Map<String, Object> getComparisonData() {
        List<TspGameResult> games = repository.findTop15ByOrderByCreatedAtDesc();
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> gamesList = new ArrayList<>();
        
        long bfSum = 0;
        long nnSum = 0;
        long dpSum = 0;
        int count = games.size();
        
        for (TspGameResult game : games) {
            Map<String, Object> gameData = new HashMap<>();
            gameData.put("gameId", game.getId());
            gameData.put("playerName", game.getPlayerName());
            gameData.put("createdAt", game.getCreatedAt());
            
            Map<String, Long> algorithmTimes = new HashMap<>();
            algorithmTimes.put("BruteForce", game.getBruteForceTimeNs());
            algorithmTimes.put("NearestNeighbor", game.getNearestNeighborTimeNs());
            algorithmTimes.put("DynamicProgramming", game.getDynamicProgrammingTimeNs());
            gameData.put("algorithmTimes", algorithmTimes);
            
            gamesList.add(gameData);
            bfSum += game.getBruteForceTimeNs();
            nnSum += game.getNearestNeighborTimeNs();
            dpSum += game.getDynamicProgrammingTimeNs();
        }
        
        Map<String, Long> averages = new HashMap<>();
        if (count > 0) {
            averages.put("BruteForce", bfSum / count);
            averages.put("NearestNeighbor", nnSum / count);
            averages.put("DynamicProgramming", dpSum / count);
        }
        
        result.put("games", gamesList);
        result.put("averages", averages);
        result.put("count", count);
        
        return result;
    }
}
