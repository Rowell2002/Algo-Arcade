package com.example.games.dto;

import java.util.List;
import java.util.Map;

public class SnakeLadderDTOs {

    public static class StartGameRequest {
        private String playerName;
        private int boardSize; // N

        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public int getBoardSize() { return boardSize; }
        public void setBoardSize(int boardSize) { this.boardSize = boardSize; }
    }

    public static class GameData {
        private String gameId;
        private String playerName;
        private int boardSize;
        // Map from Start Position -> End Position
        private Map<Integer, Integer> snakes;
        private Map<Integer, Integer> ladders;
        // 3 answer choices for the player to pick from
        private List<Integer> choices;
        // Algorithm execution times
        private Map<String, Long> algorithmTimes;

        public String getGameId() { return gameId; }
        public void setGameId(String gameId) { this.gameId = gameId; }
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public int getBoardSize() { return boardSize; }
        public void setBoardSize(int boardSize) { this.boardSize = boardSize; }
        public Map<Integer, Integer> getSnakes() { return snakes; }
        public void setSnakes(Map<Integer, Integer> snakes) { this.snakes = snakes; }
        public Map<Integer, Integer> getLadders() { return ladders; }
        public void setLadders(Map<Integer, Integer> ladders) { this.ladders = ladders; }
        public List<Integer> getChoices() { return choices; }
        public void setChoices(List<Integer> choices) { this.choices = choices; }
        public Map<String, Long> getAlgorithmTimes() { return algorithmTimes; }
        public void setAlgorithmTimes(Map<String, Long> algorithmTimes) { this.algorithmTimes = algorithmTimes; }
    }

    public static class SolveGameRequest {
        private int userGuess;

        public int getUserGuess() { return userGuess; }
        public void setUserGuess(int userGuess) { this.userGuess = userGuess; }
    }

    public static class GameResultResponse {
        private int minDiceThrows;
        private boolean isCorrect;
        private int userGuess;
        private Map<String, Long> algorithmTimes; // "BFS" -> time, "Dijkstra" -> time
        private String message;
        
        public int getMinDiceThrows() { return minDiceThrows; }
        public void setMinDiceThrows(int minDiceThrows) { this.minDiceThrows = minDiceThrows; }
        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { isCorrect = correct; }
        public int getUserGuess() { return userGuess; }
        public void setUserGuess(int userGuess) { this.userGuess = userGuess; }
        public Map<String, Long> getAlgorithmTimes() { return algorithmTimes; }
        public void setAlgorithmTimes(Map<String, Long> algorithmTimes) { this.algorithmTimes = algorithmTimes; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
