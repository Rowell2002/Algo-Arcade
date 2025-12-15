package com.example.games.dto;

import java.util.List;
import java.util.Map;

public class TspDTOs {

    public static class StartGameRequest {
        private String playerName;
        
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
    }

    public static class TspGameData {
        private String gameId;
        private String playerName;
        private String homeCity;
        // Map<String, Map<String, Integer>>: "A" -> {"B": 50, "C": 70...}
        private Map<String, Map<String, Integer>> distances;

        public String getGameId() { return gameId; }
        public void setGameId(String gameId) { this.gameId = gameId; }
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public String getHomeCity() { return homeCity; }
        public void setHomeCity(String homeCity) { this.homeCity = homeCity; }
        public Map<String, Map<String, Integer>> getDistances() { return distances; }
        public void setDistances(Map<String, Map<String, Integer>> distances) { this.distances = distances; }
    }

    public static class SolveTspRequest {
        // The cities the user *chose* to visit, in the order they visited them.
        // Should NOT include Home city at start/end, or maybe it does? 
        // Let's assume user sends the sequence of visited cities: e.g. ["B", "C", "F"]
        // The backend knows Home is the start/end.
        private List<String> visitedCities; 
        
        public List<String> getVisitedCities() { return visitedCities; }
        public void setVisitedCities(List<String> visitedCities) { this.visitedCities = visitedCities; }
    }

    public static class TspResult {
        private boolean isCorrect;
        private int userDistance;
        private int minDistance; // Optimal
        private List<String> optimalPath; // e.g. ["A", "B", "C", "A"]
        
        private Map<String, Long> algorithmTimes; // BruteForce -> 123ns, NN -> 45ns...
        private Map<String, String> algorithmComplexities; // "BruteForce" -> "O(N!)"
        
        private String message;

        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { isCorrect = correct; }
        public int getUserDistance() { return userDistance; }
        public void setUserDistance(int userDistance) { this.userDistance = userDistance; }
        public int getMinDistance() { return minDistance; }
        public void setMinDistance(int minDistance) { this.minDistance = minDistance; }
        public List<String> getOptimalPath() { return optimalPath; }
        public void setOptimalPath(List<String> optimalPath) { this.optimalPath = optimalPath; }
        public Map<String, Long> getAlgorithmTimes() { return algorithmTimes; }
        public void setAlgorithmTimes(Map<String, Long> algorithmTimes) { this.algorithmTimes = algorithmTimes; }
        public Map<String, String> getAlgorithmComplexities() { return algorithmComplexities; }
        public void setAlgorithmComplexities(Map<String, String> algorithmComplexities) { this.algorithmComplexities = algorithmComplexities; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
