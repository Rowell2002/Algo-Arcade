import axios from "axios";

const API_URL = "http://localhost:8080/api/eight-queens";

class EightQueensService {
    getStats() {
        return axios.get(`${API_URL}/stats`);
    }

    submitSolution(playerName, queens) {
        return axios.post(`${API_URL}/submit`, { playerName, queens });
    }
}

export default new EightQueensService();
