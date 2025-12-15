import axios from "axios";

const API_URL = "http://localhost:8080/api/snakeladder";

class SnakeLadderService {
  startGame(playerName, boardSize) {
    return axios.post(`${API_URL}/start`, { playerName, boardSize });
  }

  solveGame(gameId, userGuess) {
    return axios.post(`${API_URL}/solve/${gameId}`, { userGuess });
  }

  getLeaderboard() {
    return axios.get(`${API_URL}/leaderboard`);
  }
}

export default new SnakeLadderService();
