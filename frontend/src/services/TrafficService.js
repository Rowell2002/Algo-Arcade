import axios from "axios";

const API_URL = "http://localhost:8080/api/traffic";

class TrafficService {
    startGame(playerName) {
        return axios.post(`${API_URL}/start`, { playerName });
    }

    solveGame(gameId, userGuess) {
        return axios.post(`${API_URL}/solve/${gameId}`, { userGuess });
    }
}

export default new TrafficService();
