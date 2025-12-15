package com.example.games.dto;

import java.util.List;
import java.util.Map;

public class HanoiDTOs {

    public static class StartGameRequest {
        private String playerName;
        private int numPegs; // 3 or 4
        
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public int getNumPegs() { return numPegs; }
        public void setNumPegs(int numPegs) { this.numPegs = numPegs; }
    }

    public static class HanoiGameData {
        private String gameId;
        private String playerName;
        private int numDisks;
        private int numPegs;
        
        public String getGameId() { return gameId; }
        public void setGameId(String gameId) { this.gameId = gameId; }
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public int getNumDisks() { return numDisks; }
        public void setNumDisks(int numDisks) { this.numDisks = numDisks; }
        public int getNumPegs() { return numPegs; }
        public void setNumPegs(int numPegs) { this.numPegs = numPegs; }
    }

    public static class MoveRequest {
        private int fromPeg;
        private int toPeg;
        
        public int getFromPeg() { return fromPeg; }
        public void setFromPeg(int fromPeg) { this.fromPeg = fromPeg; }
        public int getToPeg() { return toPeg; }
        public void setToPeg(int toPeg) { this.toPeg = toPeg; }
    }

    public static class SolveRequest {
        private int userMinMoves;
        private List<String> userSequence; 
        
        public int getUserMinMoves() { return userMinMoves; }
        public void setUserMinMoves(int userMinMoves) { this.userMinMoves = userMinMoves; }
        public List<String> getUserSequence() { return userSequence; }
        public void setUserSequence(List<String> userSequence) { this.userSequence = userSequence; }
    }

    public static class HanoiResultResponse {
        private int optimalMinMoves;
        private boolean isCorrect;
        private int userMinMoves;
        private Map<String, Long> algorithmTimes;
        private String message;
        
        public int getOptimalMinMoves() { return optimalMinMoves; }
        public void setOptimalMinMoves(int optimalMinMoves) { this.optimalMinMoves = optimalMinMoves; }
        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { isCorrect = correct; }
        public int getUserMinMoves() { return userMinMoves; }
        public void setUserMinMoves(int userMinMoves) { this.userMinMoves = userMinMoves; }
        public Map<String, Long> getAlgorithmTimes() { return algorithmTimes; }
        public void setAlgorithmTimes(Map<String, Long> algorithmTimes) { this.algorithmTimes = algorithmTimes; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
