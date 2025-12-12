import axios from "axios";

const API_URL = "http://localhost:8080/api/tsp";

class TspService {
    startGame(playerName) {
        return axios.post(`${API_URL}/start`, { playerName });
    }

    solveGame(gameId, visitedCities) {
        return axios.post(`${API_URL}/solve/${gameId}`, { visitedCities });
    }
}

export default new TspService();
