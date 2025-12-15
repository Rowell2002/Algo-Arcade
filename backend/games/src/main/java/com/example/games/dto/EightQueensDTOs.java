package com.example.games.dto;

import java.util.List;

public class EightQueensDTOs {

    public static class SubmitRequest {
        private String playerName;
        private List<Integer> queens; 
        
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public List<Integer> getQueens() { return queens; }
        public void setQueens(List<Integer> queens) { this.queens = queens; }
    }

    public static class SubmitResponse {
        private boolean isValid;
        private boolean isUnique; 
        private String message;
        private int totalDiscovered; 
        
        public boolean isValid() { return isValid; }
        public void setValid(boolean isValid) { this.isValid = isValid; }
        public boolean isUnique() { return isUnique; }
        public void setUnique(boolean isUnique) { this.isUnique = isUnique; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public int getTotalDiscovered() { return totalDiscovered; }
        public void setTotalDiscovered(int totalDiscovered) { this.totalDiscovered = totalDiscovered; }
    }

    public static class StatsResponse {
        private long sequentialTimeNs;
        private long threadedTimeNs;
        private int totalSolutionsFound; 
        private int totalDiscoveredByPlayers;
        
        public long getSequentialTimeNs() { return sequentialTimeNs; }
        public void setSequentialTimeNs(long sequentialTimeNs) { this.sequentialTimeNs = sequentialTimeNs; }
        public long getThreadedTimeNs() { return threadedTimeNs; }
        public void setThreadedTimeNs(long threadedTimeNs) { this.threadedTimeNs = threadedTimeNs; }
        public int getTotalSolutionsFound() { return totalSolutionsFound; }
        public void setTotalSolutionsFound(int totalSolutionsFound) { this.totalSolutionsFound = totalSolutionsFound; }
        public int getTotalDiscoveredByPlayers() { return totalDiscoveredByPlayers; }
        public void setTotalDiscoveredByPlayers(int totalDiscoveredByPlayers) { this.totalDiscoveredByPlayers = totalDiscoveredByPlayers; }
    }
}
