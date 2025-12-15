package com.example.games.service;

import com.example.games.dto.SnakeLadderDTOs.*;
import com.example.games.entity.SnakeLadderGameResult;
import com.example.games.repository.SnakeLadderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SnakeLadderService {

    @Autowired
    private SnakeLadderRepository repository;

    private final Map<String, GameData> activeGames = new ConcurrentHashMap<>();

    private static final int DICE_FACES = 6;

    public GameData startGame(String playerName, int n) {
        String gameId = UUID.randomUUID().toString();
        int totalCells = n * n;
        int numSnakes = n - 2;
        int numLadders = n - 2;

        Map<Integer, Integer> snakes = new HashMap<>();
        Map<Integer, Integer> ladders = new HashMap<>();
        Set<Integer> occupied = new HashSet<>();

        // Start and End are reserved
        occupied.add(1);
        occupied.add(totalCells);

        Random rand = new Random();

        // Generate Snakes (Top -> Bottom)
        // Snake head must be > tail
        for (int i = 0; i < numSnakes; i++) {
            int head, tail;
            int attempts = 0;
            do {
                head = rand.nextInt(totalCells - 2) + 2; // 2 to totalCards-1
                tail = rand.nextInt(totalCells - 2) + 2;
                attempts++;
            } while ((head <= tail || occupied.contains(head) || occupied.contains(tail) || Math.abs(head - tail) < n) && attempts < 100);

            if (attempts < 100) {
                snakes.put(head, tail);
                occupied.add(head);
                occupied.add(tail);
            }
        }

        // Generate Ladders (Bottom -> Top)
        // Ladder bottom must be < top
        for (int i = 0; i < numLadders; i++) {
            int bottom, top;
            int attempts = 0;
            do {
                bottom = rand.nextInt(totalCells - 2) + 2;
                top = rand.nextInt(totalCells - 2) + 2;
                attempts++;
            } while ((bottom >= top || occupied.contains(bottom) || occupied.contains(top) || Math.abs(top - bottom) < n) && attempts < 100);

            if (attempts < 100) {
                ladders.put(bottom, top);
                occupied.add(bottom);
                occupied.add(top);
            }
        }

        GameData gameData = new GameData();
        gameData.setGameId(gameId);
        gameData.setPlayerName(playerName);
        gameData.setBoardSize(n);
        gameData.setSnakes(snakes);
        gameData.setLadders(ladders);

        // Calculate correct answer for generating choices (quick calculation, not timed)
        int correctAnswer = solveBFS(gameData);

        // Generate 3 choices: 1 correct + 2 decoys
        List<Integer> choices = new ArrayList<>();
        choices.add(correctAnswer);

        // Generate unique decoys
        Set<Integer> usedValues = new HashSet<>();
        usedValues.add(correctAnswer);

        while (choices.size() < 3) {
            // Random offset between -3 and +3, avoiding 0
            int offset = rand.nextInt(6) - 3;
            if (offset == 0) offset = rand.nextBoolean() ? 1 : -1;

            int decoy = correctAnswer + offset;
            if (decoy > 0 && !usedValues.contains(decoy)) {
                choices.add(decoy);
                usedValues.add(decoy);
            }
        }

        // Shuffle choices
        Collections.shuffle(choices);
        gameData.setChoices(choices);

        activeGames.put(gameId, gameData);
        return gameData;
    }

    public GameResultResponse solveGame(String gameId, int userGuess) {
        GameData game = activeGames.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        Map<String, Long> times = new HashMap<>();

        // 1. BFS Algorithm with timing
        long startBFS = System.nanoTime();
        int correctAnswer = solveBFS(game);
        long endBFS = System.nanoTime();
        times.put("BFS", (endBFS - startBFS) / 1000); // Convert to microseconds

        // 2. Dijkstra Algorithm with timing
        long startDijkstra = System.nanoTime();
        solveDijkstra(game);
        long endDijkstra = System.nanoTime();
        times.put("Dijkstra", (endDijkstra - startDijkstra) / 1000); // Convert to microseconds

        // Save result to database immediately (like Traffic game)
        SnakeLadderGameResult result = new SnakeLadderGameResult();
        // Only save player name if answer is correct
        result.setPlayerName(userGuess == correctAnswer ? game.getPlayerName() : null);
        result.setBoardSize(game.getBoardSize());
        result.setMinDiceThrows(correctAnswer);
        result.setUserGuess(userGuess);
        result.setBfsTime((endBFS - startBFS) / 1000); // Microseconds
        result.setDijkstraTime((endDijkstra - startDijkstra) / 1000); // Microseconds

        repository.save(result);

        // Prepare response
        GameResultResponse response = new GameResultResponse();
        response.setMinDiceThrows(correctAnswer);
        response.setUserGuess(userGuess);
        response.setCorrect(userGuess == correctAnswer);
        response.setAlgorithmTimes(times);
        response.setMessage(response.isCorrect() ? "Correct! Well done." : "Incorrect. The minimum throws needed was " + correctAnswer);

        return response;
    }


    /**
     * Helper to get next position after applying snakes/ladders.
     * Matches Android implementation's Board.getDestination() method.
     */
    private int getDestination(int current, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
        if (snakes.containsKey(current)) {
            return snakes.get(current);
        }
        if (ladders.containsKey(current)) {
            return ladders.get(current);
        }
        return current;
    }

    /**
     * BFS Algorithm - Following Android implementation exactly.
     * Uses Queue with QueueEntry pattern (position, distance).
     */
    private int solveBFS(GameData game) {
        int boardSize = game.getBoardSize();
        int totalCells = boardSize * boardSize;
        int endPos = totalCells;

        // Initialize visited array
        boolean[] visited = new boolean[totalCells + 1];

        // Queue stores: [position, distance]
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{1, 0}); // Start at position 1 with 0 throws
        visited[1] = true;

        // BFS traversal
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int position = current[0];
            int distance = current[1];

            // Try all 6 dice outcomes (1 to 6)
            for (int dice = 1; dice <= DICE_FACES; dice++) {
                int nextPos = position + dice;

                // Skip if next position exceeds total cells
                if (nextPos > totalCells) {
                    continue;
                }

                // Apply snake/ladder transition
                int finalPos = getDestination(nextPos, game.getSnakes(), game.getLadders());

                // Check if we reached the end
                if (finalPos == endPos) {
                    return distance + 1;
                }

                // If not visited, mark as visited and add to queue
                if (!visited[finalPos]) {
                    visited[finalPos] = true;
                    queue.add(new int[]{finalPos, distance + 1});
                }
            }
        }

        // No path found (should not happen in a valid game)
        return -1;
    }

    /**
     * Dijkstra's Algorithm - Following Android implementation exactly.
     * Uses PriorityQueue with distance tracking.
     */
    private int solveDijkstra(GameData game) {
        int boardSize = game.getBoardSize();
        int totalCells = boardSize * boardSize;
        int endPos = totalCells;

        // Initialize distance array with MAX_VALUE
        int[] distance = new int[totalCells + 1];
        Arrays.fill(distance, Integer.MAX_VALUE);
        distance[1] = 0;

        // Initialize visited array
        boolean[] visited = new boolean[totalCells + 1];

        // Priority queue: [position, distance] - sorted by distance (min-heap)
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{1, 0}); // Start at position 1 with distance 0

        // Dijkstra's algorithm
        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int currentPos = current[0];
            int currentDist = current[1];

            // Skip if already visited
            if (visited[currentPos]) {
                continue;
            }

            // Mark as visited
            visited[currentPos] = true;

            // If reached end position, return the distance
            if (currentPos == endPos) {
                return distance[endPos];
            }

            // Try all 6 dice outcomes (1 to 6)
            for (int dice = 1; dice <= DICE_FACES; dice++) {
                int nextPos = currentPos + dice;

                // Skip if next position exceeds total cells
                if (nextPos > totalCells) {
                    continue;
                }

                // Apply snake/ladder transition
                int finalPos = getDestination(nextPos, game.getSnakes(), game.getLadders());

                // If not visited, try to update distance
                if (!visited[finalPos]) {
                    int newDistance = distance[currentPos] + 1;

                    // Update distance if we found a shorter path
                    if (newDistance < distance[finalPos]) {
                        distance[finalPos] = newDistance;
                        pq.add(new int[]{finalPos, newDistance});
                    }
                }
            }
        }

        // Return the distance to end position
        return distance[endPos] == Integer.MAX_VALUE ? -1 : distance[endPos];
    }

    public Map<String, Object> getComparisonData() {
        List<SnakeLadderGameResult> games = repository.findTop15ByOrderByCreatedAtDesc();

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> gamesList = new ArrayList<>();

        long bfsSum = 0;
        long dijkstraSum = 0;
        int count = games.size();

        for (SnakeLadderGameResult game : games) {
            Map<String, Object> gameData = new HashMap<>();
            gameData.put("gameId", game.getId());
            gameData.put("playerName", game.getPlayerName());
            gameData.put("createdAt", game.getCreatedAt());

            Map<String, Long> algorithmTimes = new HashMap<>();
            algorithmTimes.put("BFS", game.getBfsTime());
            algorithmTimes.put("Dijkstra", game.getDijkstraTime());
            gameData.put("algorithmTimes", algorithmTimes);

            gamesList.add(gameData);
            bfsSum += game.getBfsTime();
            dijkstraSum += game.getDijkstraTime();
        }

        Map<String, Long> averages = new HashMap<>();
        if (count > 0) {
            averages.put("BFS", bfsSum / count);
            averages.put("Dijkstra", dijkstraSum / count);
        }

        result.put("games", gamesList);
        result.put("averages", averages);
        result.put("count", count);

        return result;
    }

    public List<Map<String, Object>> getLeaderboard() {
        List<SnakeLadderGameResult> results = repository.findTop20ByPlayerNameIsNotNullOrderByBoardSizeDescBfsTimeAsc();
        List<Map<String, Object>> leaderboard = new ArrayList<>();

        for (SnakeLadderGameResult result : results) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("playerName", result.getPlayerName());
            entry.put("boardSize", result.getBoardSize());
            entry.put("minDiceThrows", result.getMinDiceThrows());
            entry.put("bfsTime", result.getBfsTime());
            entry.put("dijkstraTime", result.getDijkstraTime());
            entry.put("createdAt", result.getCreatedAt());
            leaderboard.add(entry);
        }

        return leaderboard;
    }
}
