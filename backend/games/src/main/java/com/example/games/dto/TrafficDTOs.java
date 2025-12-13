package com.example.games.dto;

import java.util.List;
import java.util.Map;

public class TrafficDTOs {

    public static class StartGameRequest {
        private String playerName;

        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
    }

    public static class EdgeDTO {
        private String from;
        private String to;
        private int capacity;
        
        public EdgeDTO() {}

        public EdgeDTO(String from, String to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
        }

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
        public int getCapacity() { return capacity; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
    }

    public static class TrafficGameData {
        private String gameId;
        private String playerName;
        private List<EdgeDTO> edges;

        public String getGameId() { return gameId; }
        public void setGameId(String gameId) { this.gameId = gameId; }
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public List<EdgeDTO> getEdges() { return edges; }
        public void setEdges(List<EdgeDTO> edges) { this.edges = edges; }
    }

    public static class SolveRequest {
        private int userGuess;

        public int getUserGuess() { return userGuess; }
        public void setUserGuess(int userGuess) { this.userGuess = userGuess; }
    }

    public static class TrafficResultResponse {
        private int maxFlow;
        private boolean isCorrect;
        private int userGuess;
        private Map<String, Long> algorithmTimes;
        private String message;

        public int getMaxFlow() { return maxFlow; }
        public void setMaxFlow(int maxFlow) { this.maxFlow = maxFlow; }
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
