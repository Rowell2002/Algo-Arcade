import axios from "axios";

const API_URL = "http://localhost:8080/api/hanoi";

class HanoiService {
    startGame(playerName, numPegs) {
        return axios.post(`${API_URL}/start`, { playerName, numPegs });
    }

    solveGame(gameId, userMinMoves, userSequence) {
        return axios.post(`${API_URL}/solve/${gameId}`, { userMinMoves, userSequence });
    }
}

export default new HanoiService();
