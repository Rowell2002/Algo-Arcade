package com.example.games.service;

import com.example.games.dto.EightQueensDTOs.*;
import com.example.games.entity.EightQueensSolution;
import com.example.games.entity.EightQueensStats;
import com.example.games.repository.EightQueensSolutionRepository;
import com.example.games.repository.EightQueensStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class EightQueensService {

    @Autowired
    private EightQueensSolutionRepository solutionRepository;

    @Autowired
    private EightQueensStatsRepository statsRepository;

    // We can cache the total solutions count if we want, or query DB.
    // 92 is the constant known solution count for 8-Queens.
    private static final int TOTAL_SOLUTIONS = 92;

    public StatsResponse getStats() {
        // Find latest stats or run solvers if not present
        List<EightQueensStats> statsList = statsRepository.findAll();
        EightQueensStats stats;
        
        if (statsList.isEmpty()) {
            stats = runSolversAndSaveStats();
        } else {
            // Get most recent
            stats = statsList.get(statsList.size() - 1);
        }

        long count = solutionRepository.count();

        StatsResponse response = new StatsResponse();
        response.setSequentialTimeNs(stats.getSequentialTimeNs());
        response.setThreadedTimeNs(stats.getThreadedTimeNs());
        response.setTotalSolutionsFound(TOTAL_SOLUTIONS);
        response.setTotalDiscoveredByPlayers((int) count);
        return response;
    }

    public SubmitResponse submitSolution(SubmitRequest request) {
        List<Integer> queens = request.getQueens();
        
        // 1. Validate Board
        if (!isValidPlacement(queens)) {
            SubmitResponse res = new SubmitResponse();
            res.setValid(false);
            res.setMessage("Invalid Configuration: Queens are threatening each other.");
            return res;
        }

        // 2. Canonical String
        String solutionStr = toCanonicalString(queens);

        // 3. Check uniqueness
        Optional<EightQueensSolution> existing = solutionRepository.findBySolutionString(solutionStr);
        
        SubmitResponse res = new SubmitResponse();
        res.setValid(true);

        // Run solvers and save stats for each valid submission (for comparison data)
        runSolversAndSaveStats();

        if (existing.isPresent()) {
            res.setUnique(false);
            res.setMessage("Valid! But this solution was already discovered by " + existing.get().getDiscoveredBy());
            res.setTotalDiscovered((int) solutionRepository.count());
        } else {
            // New Discovery
            EightQueensSolution sol = new EightQueensSolution();
            sol.setSolutionString(solutionStr);
            sol.setDiscoveredBy(request.getPlayerName());
            solutionRepository.save(sol);
            
            long total = solutionRepository.count();
            res.setUnique(true);
            res.setTotalDiscovered((int) total);
            
            if (total >= TOTAL_SOLUTIONS) {
                res.setMessage("CONGRATULATIONS! You found the final solution! The game will now reset.");
                // Reset logic
                solutionRepository.deleteAll();
            } else {
                res.setMessage("Valid! New Solution Discovered!");
            }
        }
        return res;
    }

    private EightQueensStats runSolversAndSaveStats() {
        // 1. Sequential
        long startSeq = System.nanoTime();
        List<List<Integer>> seqSolutions = solveSequential();
        long endSeq = System.nanoTime();
        
        // 2. Threaded
        long startThread = System.nanoTime();
        List<List<Integer>> threadSolutions = solveThreaded();
        long endThread = System.nanoTime();

        EightQueensStats stats = new EightQueensStats();
        stats.setSequentialTimeNs(endSeq - startSeq);
        stats.setThreadedTimeNs(endThread - startThread);
        stats.setRunAt(LocalDateTime.now());
        
        return statsRepository.save(stats);
    }
    
    // --- Solvers ---

    // Sequential Backtracking
    public List<List<Integer>> solveSequential() {
        List<List<Integer>> solutions = new ArrayList<>();
        placeQueen(0, new int[8], solutions);
        return solutions;
    }

    private void placeQueen(int row, int[] queens, List<List<Integer>> solutions) {
        if (row == 8) {
            List<Integer> sol = new ArrayList<>();
            for (int q : queens) sol.add(q);
            solutions.add(sol);
            return;
        }

        for (int col = 0; col < 8; col++) {
            if (isSafe(row, col, queens)) {
                queens[row] = col;
                placeQueen(row + 1, queens, solutions);
            }
        }
    }

    // Threaded Backtracking
    // Divide the work based on the first queen's placement (Row 0 has 8 possible columns)
    public List<List<Integer>> solveThreaded() {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<Future<List<List<Integer>>>> futures = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            final int firstCol = i;
            futures.add(executor.submit(() -> {
                List<List<Integer>> partialSolutions = new ArrayList<>();
                int[] queens = new int[8];
                queens[0] = firstCol;
                placeQueenThreaded(1, queens, partialSolutions);
                return partialSolutions;
            }));
        }

        List<List<Integer>> allSolutions = new ArrayList<>();
        for (Future<List<List<Integer>>> f : futures) {
            try {
                allSolutions.addAll(f.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        return allSolutions;
    }

    private void placeQueenThreaded(int row, int[] queens, List<List<Integer>> solutions) {
        if (row == 8) {
            // Need thread-safe collection? or just return local list?
            // Since we collect into local partialSolutions, it is fine.
            List<Integer> sol = new ArrayList<>();
            for (int q : queens) sol.add(q);
            solutions.add(sol);
            return;
        }
        
        // We need a separate board array for each recursion path in threaded context?
        // Actually backtracking uses the same array usually. 
        // But since each task has its own 'queens' array passed from root, 
        // and recursive calls modify it.
        // Wait, standard backtracking modifies array and then restores it (backtracks).
        // Since we are single-threaded WITHIN the task, passing the array is fine.
        
        for (int col = 0; col < 8; col++) {
            if (isSafe(row, col, queens)) {
                queens[row] = col;
                placeQueenThreaded(row + 1, queens, solutions);
                // No un-set needed for array logic if we overwrite?
                // Actually need to ensure next iteration doesn't see old value if logic depends on it.
                // Our isSafe only checks columns < row. So valid.
            }
        }
    }

    // Helper
    private boolean isSafe(int row, int col, int[] queens) {
        for (int i = 0; i < row; i++) {
            int placedCol = queens[i];
            if (placedCol == col) return false; // Same column
            if (Math.abs(row - i) == Math.abs(col - placedCol)) return false; // Diagonal
        }
        return true;
    }

    // --- Validation for User Input ---
    
    // Input: List<Integer> of size 8. Index = Row, Value = Col.
    // Example: [0, 4, 7, 5, 2, 6, 1, 3]
    private boolean isValidPlacement(List<Integer> queens) {
        if (queens == null || queens.size() != 8) return false;
        
        // Convert to array for helper
        int[] qArr = queens.stream().mapToInt(i -> i).toArray();
        
        // Check constraints:
        // 1. Bounds 0-7
        // 2. No two queens attack
        
        for (int r = 0; r < 8; r++) {
            if (qArr[r] < 0 || qArr[r] > 7) return false;
            // Check against previous rows? No, need to check against ALL other rows for user input validation.
            // Actually, internal solver generates row-by-row.
            // User input is full board.
            // Check every pair.
            
            for (int r2 = r + 1; r2 < 8; r2++) {
                int c1 = qArr[r];
                int c2 = qArr[r2];
                
                if (c1 == c2) return false; // Same column
                if (Math.abs(r - r2) == Math.abs(c1 - c2)) return false; // Diagonal
            }
        }
        return true;
    }

    private String toCanonicalString(List<Integer> queens) {
        StringBuilder sb = new StringBuilder();
        for (int c : queens) sb.append(c);
        return sb.toString();
    }
    
    public Map<String, Object> getComparisonData() {
        List<EightQueensStats> statsList = statsRepository.findTop15ByOrderByRunAtDesc();
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> gamesList = new ArrayList<>();
        
        long seqSum = 0;
        long threadSum = 0;
        int count = statsList.size();
        
        for (EightQueensStats stats : statsList) {
            Map<String, Object> gameData = new HashMap<>();
            gameData.put("gameId", stats.getId());
            gameData.put("runAt", stats.getRunAt());
            
            Map<String, Long> algorithmTimes = new HashMap<>();
            algorithmTimes.put("Sequential", stats.getSequentialTimeNs());
            algorithmTimes.put("Threaded", stats.getThreadedTimeNs());
            gameData.put("algorithmTimes", algorithmTimes);
            
            gamesList.add(gameData);
            seqSum += stats.getSequentialTimeNs() != null ? stats.getSequentialTimeNs() : 0;
            threadSum += stats.getThreadedTimeNs() != null ? stats.getThreadedTimeNs() : 0;
        }
        
        Map<String, Long> averages = new HashMap<>();
        if (count > 0) {
            averages.put("Sequential", seqSum / count);
            averages.put("Threaded", threadSum / count);
        }
        
        result.put("games", gamesList);
        result.put("averages", averages);
        result.put("count", count);
        
        return result;
    }
}
