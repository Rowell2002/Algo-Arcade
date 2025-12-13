package com.example.games.service;

import com.example.games.dto.HanoiDTOs.*;
import com.example.games.entity.HanoiGameResult;
import com.example.games.repository.HanoiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HanoiService {

    @Autowired
    private HanoiRepository repository;

    private final Map<String, HanoiGameData> activeGames = new ConcurrentHashMap<>();

    public HanoiGameData startGame(String playerName, int numPegs) {
        String gameId = UUID.randomUUID().toString();
        
        // Random disks 5-10
        int n = new Random().nextInt(6) + 5; 
        
        HanoiGameData data = new HanoiGameData();
        data.setGameId(gameId);
        data.setPlayerName(playerName);
        data.setNumDisks(n);
        data.setNumPegs(numPegs);

        activeGames.put(gameId, data);
        return data;
    }

    public HanoiResultResponse solveGame(String gameId, int userMinMoves, List<String> userSequence) {
        HanoiGameData game = activeGames.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        Map<String, Long> times = new HashMap<>();
        int optimalMoves = 0;

        if (game.getNumPegs() == 3) {
            // 3-Peg Algorithms
            
            // 1. Recursive
            long startRec = System.nanoTime();
            optimalMoves = solveRecursive3(game.getNumDisks());
            long endRec = System.nanoTime();
            times.put("Recursive", (endRec - startRec) / 1000); // microseconds

            // 2. Iterative
            long startIter = System.nanoTime();
            solveIterative3(game.getNumDisks());
            long endIter = System.nanoTime();
            times.put("Iterative", (endIter - startIter) / 1000);
        
        } else {
            // 4-Peg Algorithms
            
            // 1. Frame-Stewart
            long startFS = System.nanoTime();
            optimalMoves = solveFrameStewart(game.getNumDisks(), 4);
            long endFS = System.nanoTime();
            times.put("FrameStewart", (endFS - startFS) / 1000);

            // 2. BFS (Shortest Path)
            // BFS is expensive for N=10 (4^10 states), but required "2 algorithms".
            // Frame-Stewart is O(1) formula basically. BFS verifies it's truly optimal.
            // Limit BFS for safety? N=10 4 pegs is fast (Frame Stewart ~49 moves). 
            // State space is manageable.
            long startBFS = System.nanoTime();
            // BFS can take time, let's limit if N is large or just run it. 
            // For N=10, 4 Pegs, BFS is reasonably fast enough for a demo.
            int bfsMoves = solveBFS4(game.getNumDisks());
            long endBFS = System.nanoTime();
            times.put("BFS", (endBFS - startBFS) / 1000);
            
            // Frame Stewart provides optimal number of moves, BFS confirms it.
            // We'll use the FrameStewart result as the "Optimal" baseline usually, 
            // but BFS is the guaranteed shortest path finding algo.
            optimalMoves = bfsMoves; 
        }

        boolean isCorrect = (userMinMoves == optimalMoves);
        // Can also validate userSequence size against optimalMoves
        
        // Save
        HanoiGameResult result = new HanoiGameResult();
        // Only save player name if answer is correct
        result.setPlayerName(isCorrect ? game.getPlayerName() : null);
        result.setNumDisks(game.getNumDisks());
        result.setNumPegs(game.getNumPegs());
        result.setUserMinMoves(userMinMoves);
        result.setOptimalMinMoves(optimalMoves);
        // Just store length of sequence or first few moves if string is too long
        if (userSequence != null) {
             result.setUserSequence("Count: " + userSequence.size());
        }
        
        if (game.getNumPegs() == 3) {
            result.setAlgo1Time(times.get("Recursive"));
            result.setAlgo2Time(times.get("Iterative"));
        } else {
            result.setAlgo1Time(times.get("FrameStewart"));
            result.setAlgo2Time(times.get("BFS"));
        }
        
        repository.save(result);

        HanoiResultResponse response = new HanoiResultResponse();
        response.setOptimalMinMoves(optimalMoves);
        response.setUserMinMoves(userMinMoves);
        response.setCorrect(isCorrect);
        response.setAlgorithmTimes(times);
        response.setMessage(isCorrect ? "Correct! Optimal moves found." : "Incorrect. Optimal moves: " + optimalMoves);
        
        return response;
    }

    // --- 3 Pegs Algorithms ---

    // 1. Recursive: Actual recursive implementation counting moves
    private int solveRecursive3(int n) {
        return hanoi3Recursive(n, 0, 2, 1); // Move n disks from peg 0 to peg 2 using peg 1
    }
    
    private int hanoi3Recursive(int n, int source, int dest, int aux) {
        if (n == 0) return 0;
        int moves = 0;
        moves += hanoi3Recursive(n - 1, source, aux, dest); // Move n-1 disks to aux
        moves += 1; // Move the nth disk to dest
        moves += hanoi3Recursive(n - 1, aux, dest, source); // Move n-1 disks from aux to dest
        return moves;
    }

    // 2. Iterative: Stack-based simulation of moves
    private int solveIterative3(int n) {
        // Simulate using explicit stack
        // Each task: (numDisks, source, dest, aux)
        java.util.Stack<int[]> stack = new java.util.Stack<>();
        stack.push(new int[]{n, 0, 2, 1}); // Move n disks from peg 0 to peg 2
        
        int moveCount = 0;
        
        while (!stack.isEmpty()) {
            int[] task = stack.pop();
            int disks = task[0];
            int src = task[1];
            int dst = task[2];
            int aux = task[3];
            
            if (disks == 1) {
                moveCount++; // Direct move
            } else if (disks > 1) {
                // Push in reverse order (so they execute in correct order)
                // 3. Move n-1 from aux to dest
                stack.push(new int[]{disks - 1, aux, dst, src});
                // 2. Move 1 from source to dest
                stack.push(new int[]{1, src, dst, aux});
                // 1. Move n-1 from source to aux
                stack.push(new int[]{disks - 1, src, aux, dst});
            }
        }
        
        return moveCount;
    }

    // --- 4 Pegs Algorithms ---

    // 1. Frame-Stewart Algorithm
    // dp[n][k] = min( 2*dp[n-r][k] + dp[r][k-1] ) for 1 <= r < n
    private int solveFrameStewart(int n, int k) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        if (k == 3) return (1 << n) - 1;

        int minMoves = Integer.MAX_VALUE;

        // Try splitting at every possible r
        for (int r = 1; r < n; r++) {
            // Move n-r disks to temp peg using k pegs
            // moves = 2 * T(n-r, k) + T(r, k-1)
            // Wait, standard FS is: Move top n-r disks to i using k pegs.
            // Move bottom r disks to dest using k-1 pegs.
            // Move top n-r disks to dest using k pegs.
            // T(n, k) = 2 * T(n-r, k) + T(r, k-1)
            // No, the bottom r disks are moved to dest using k-1 pegs? NO.
            // Actually: Move top k disks (where k is split point).
            // Common formula: T(n, 4) = min( 2*T(n-k, 4) + T(k, 3) )
            
            // Let's use `i` as the number of disks to move to intermediate
            // 2 * solve(n-i, 4) + solve(i, 3)
            // where i [1..n-1]
            
            int val = 2 * solveFrameStewart(n - r, k) + solveRecursive3(r);
            if (val < minMoves) minMoves = val;
        }
        return minMoves;
    }
    
    // 2. BFS for Shortest Path (N disks, 4 Pegs)
    private int solveBFS4(int n) {
        // State: List of 4 Stacks? Or simpler representation.
        // Array of sets? Or just an array `int[n]` where index is disk size (0..n-1) and value is Peg (0..3).
        // Since disks are distinct size, `int[n]` is enough. 
        // Valid move: disk `d` on peg `p` can move to peg `q` if `d` < top(q).
        
        class State {
            int[] pegOfDisk; // index 0 is smallest disk, n-1 is largest
            int dist;
            
            State(int[] p, int d) {
                this.pegOfDisk = p.clone();
                this.dist = d;
            }
            
            boolean isGoal() {
                for (int p : pegOfDisk) if (p != 3) return false; // Goal is Peg 3 (D)
                return true;
            }
            
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                State state = (State) o;
                return Arrays.equals(pegOfDisk, state.pegOfDisk);
            }

            @Override
            public int hashCode() {
                return Arrays.hashCode(pegOfDisk);
            }
            
            // Get top disk of each peg (or -1 if empty)
            int[] getTops() {
                int[] tops = new int[4];
                Arrays.fill(tops, Integer.MAX_VALUE);
                // Iterate from largest to smallest disk to find who's on top
                for (int d = n - 1; d >= 0; d--) {
                    tops[pegOfDisk[d]] = d;
                }
                return tops;
            }
        }

        int[] start = new int[n];
        Arrays.fill(start, 0); // All on Peg 0 (A)
        
        Queue<State> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        
        State initState = new State(start, 0);
        queue.add(initState);
        visited.add(Arrays.toString(start));
        
        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (current.isGoal()) return current.dist;
            
            int[] tops = current.getTops();
            
            // Try moving top of each peg to every other peg
            for (int fromPeg = 0; fromPeg < 4; fromPeg++) {
                int diskToMove = tops[fromPeg];
                if (diskToMove == Integer.MAX_VALUE) continue; // Empty peg
                
                for (int toPeg = 0; toPeg < 4; toPeg++) {
                    if (fromPeg == toPeg) continue;
                    
                    int topOfDest = tops[toPeg];
                    if (diskToMove < topOfDest) {
                        // Valid move
                        int[] nextPegs = current.pegOfDisk.clone();
                        nextPegs[diskToMove] = toPeg;
                        
                        String key = Arrays.toString(nextPegs);
                        if (!visited.contains(key)) {
                            visited.add(key);
                            queue.add(new State(nextPegs, current.dist + 1));
                        }
                    }
                }
            }
        }
        return -1;
    }
    
    public Map<String, Object> getComparisonData() {
        List<HanoiGameResult> games = repository.findTop15ByOrderByCreatedAtDesc();
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> gamesList = new ArrayList<>();
        
        // Separate counters for 3-peg and 4-peg algorithms
        long recursiveSum = 0, iterativeSum = 0;
        long frameStewartSum = 0, bfsSum = 0;
        int count3Peg = 0, count4Peg = 0;
        
        for (HanoiGameResult game : games) {
            Map<String, Object> gameData = new HashMap<>();
            gameData.put("gameId", game.getId());
            gameData.put("playerName", game.getPlayerName());
            gameData.put("createdAt", game.getCreatedAt());
            gameData.put("numPegs", game.getNumPegs());
            
            Map<String, Long> algorithmTimes = new HashMap<>();
            if (game.getNumPegs() == 3) {
                algorithmTimes.put("Recursive (3-peg)", game.getAlgo1Time());
                algorithmTimes.put("Iterative (3-peg)", game.getAlgo2Time());
                recursiveSum += game.getAlgo1Time();
                iterativeSum += game.getAlgo2Time();
                count3Peg++;
            } else {
                algorithmTimes.put("FrameStewart (4-peg)", game.getAlgo1Time());
                // BFS removed from comparison
                frameStewartSum += game.getAlgo1Time();
                count4Peg++;
            }
            gameData.put("algorithmTimes", algorithmTimes);
            
            gamesList.add(gameData);
        }
        
        // Compute averages for 3 algorithms (BFS removed)
        Map<String, Long> averages = new LinkedHashMap<>(); // Preserve order
        if (count3Peg > 0) {
            averages.put("Recursive (3-peg)", recursiveSum / count3Peg);
            averages.put("Iterative (3-peg)", iterativeSum / count3Peg);
        }
        if (count4Peg > 0) {
            averages.put("FrameStewart (4-peg)", frameStewartSum / count4Peg);
        }
        
        result.put("games", gamesList);
        result.put("averages", averages);
        result.put("count", games.size());
        result.put("count3Peg", count3Peg);
        result.put("count4Peg", count4Peg);
        
        return result;
    }
}
