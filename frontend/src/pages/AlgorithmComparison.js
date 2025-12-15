import axios from 'axios';
import {
    BarElement,
    CategoryScale,
    Chart as ChartJS,
    Legend,
    LinearScale,
    LineElement,
    PointElement,
    Title,
    Tooltip
} from 'chart.js';
import { useEffect, useState } from 'react';
import { Bar, Line } from 'react-chartjs-2';
import '../App.css';
import GameHero from '../components/GameHero';
import './AlgorithmComparison.css';

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
);

const GAMES = [
    { id: 'snakeladder', name: 'Snake & Ladder', icon: '/assets/icons/snake-ladder.png', color: '#00f3ff', gradient: 'var(--gradient-blue)' },
    { id: 'traffic', name: 'Traffic Simulation', icon: '/assets/icons/traffic.png', color: '#ff0055', gradient: 'var(--gradient-pink)' },
    { id: 'tsp', name: 'Traveling Salesman', icon: '/assets/icons/tsp.png', color: '#ffd700', gradient: 'var(--gradient-sunset)' },
    { id: 'hanoi', name: 'Tower of Hanoi', icon: '/assets/icons/hanoi.png', color: '#00ff9d', gradient: 'var(--gradient-green)' },
    { id: 'eight-queens', name: 'Eight Queens', icon: '/assets/icons/queens.png', color: '#bc13fe', gradient: 'var(--gradient-purple)' }
];

function AlgorithmComparison() {
    const [selectedGame, setSelectedGame] = useState('snakeladder');
    const [comparisonData, setComparisonData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const currentGame = GAMES.find(g => g.id === selectedGame);

    useEffect(() => {
        fetchComparisonData();
    }, [selectedGame]);

    const fetchComparisonData = async () => {
        setLoading(true);
        setError('');
        try {
            const response = await axios.get(`http://localhost:8080/api/${selectedGame}/comparison`);
            setComparisonData(response.data);
        } catch (err) {
            setError('Failed to load comparison data. Please ensure you have played some games first.');
            setComparisonData(null);
        } finally {
            setLoading(false);
        }
    };

    const getChartData = () => {
        if (!comparisonData || !comparisonData.averages) return null;

        const algorithms = Object.keys(comparisonData.averages);
        const values = Object.values(comparisonData.averages);

        // Use same 3 colors for all games (Cyan, Purple, Green)
        const getBarColor = (algo, index) => {
            const colors = [
                { bg: 'rgba(0, 243, 255, 0.6)', border: '#00f3ff' },   // Cyan
                { bg: 'rgba(188, 19, 254, 0.6)', border: '#bc13fe' },  // Purple
                { bg: 'rgba(0, 255, 157, 0.6)', border: '#00ff9d' },   // Green
            ];
            return colors[index % colors.length];
        };

        return {
            labels: algorithms,
            datasets: [{
                label: 'Average Execution Time (μs)',
                data: values,
                backgroundColor: algorithms.map((algo, i) => getBarColor(algo, i).bg),
                borderColor: algorithms.map((algo, i) => getBarColor(algo, i).border),
                borderWidth: 2,
            }]
        };
    };

    const getTrendData = () => {
        if (!comparisonData || !comparisonData.games) return null;

        const games = comparisonData.games.slice().reverse(); // Oldest to newest

        // Collect ALL unique algorithms across all games
        const allAlgorithms = new Set();
        games.forEach(game => {
            Object.keys(game.algorithmTimes).forEach(algo => allAlgorithms.add(algo));
        });
        const algorithms = Array.from(allAlgorithms);

        // Get color based on algorithm index (same colors as TSP)
        const getLineColor = (algo, index) => {
            const colors = [
                { border: '#00f3ff', bg: 'rgba(0, 243, 255, 0.6)' },   // Cyan
                { border: '#bc13fe', bg: 'rgba(188, 19, 254, 0.6)' },  // Purple
                { border: '#00ff9d', bg: 'rgba(0, 255, 157, 0.6)' },   // Green
            ];
            return colors[index % colors.length];
        };

        const datasets = algorithms.map((algo, index) => ({
            label: algo,
            data: games.map(game => game.algorithmTimes[algo] ?? null), // null for missing
            borderColor: getLineColor(algo, index).border,
            backgroundColor: getLineColor(algo, index).bg,
            tension: 0.4,
            borderWidth: 2,
            pointRadius: 6,
            pointHoverRadius: 8,
            showLine: selectedGame !== 'hanoi', // Dots only for Hanoi
        }));

        return {
            labels: games.map((_, i) => `Game ${i + 1}`),
            datasets
        };
    };

    const chartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                labels: {
                    color: '#ffffff',
                    font: { size: 12, family: 'Inter' }
                }
            },
            tooltip: {
                backgroundColor: 'rgba(0, 0, 0, 0.8)',
                titleColor: '#00f3ff',
                bodyColor: '#ffffff',
                borderColor: '#00f3ff',
                borderWidth: 1,
            }
        },
        scales: {
            y: {
                ticks: { color: '#d0d0d0' },
                grid: { color: 'rgba(255, 255, 255, 0.1)' }
            },
            x: {
                ticks: { color: '#d0d0d0' },
                grid: { color: 'rgba(255, 255, 255, 0.1)' }
            }
        }
    };

    return (
        <div className="page">
            <GameHero
                title="Algorithm Comparison"
                subtitle="Performance Analysis & Benchmarking"
                icon={currentGame?.icon}
                gradient={currentGame?.gradient}
                color={currentGame?.color}
            />

            <div className="comparison-container">
                <div className="glass-panel game-selector-panel">
                    <label>Select Game:</label>
                    <div className="game-buttons-container">
                        {GAMES.map(game => (
                            <button
                                key={game.id}
                                className={`game-icon-button ${selectedGame === game.id ? 'selected' : ''}`}
                                onClick={() => setSelectedGame(game.id)}
                                title={game.name}
                            >
                                <img src={game.icon} alt={game.name} />
                                <span className="game-name">{game.name}</span>
                            </button>
                        ))}
                    </div>
                </div>

                {loading && (
                    <div className="glass-panel">
                        <div className="skeleton skeleton-card"></div>
                    </div>
                )}

                {error && (
                    <div className="glass-panel error-panel">
                        <p>{error}</p>
                    </div>
                )}

                {!loading && !error && comparisonData && (
                    <>
                        <div className="stats-summary glass-panel">
                            <h3>Summary</h3>
                            <p>Analyzing last <strong>{comparisonData.count}</strong> game(s)</p>
                            {selectedGame === 'hanoi' && (
                                <div style={{ marginTop: '15px' }}>
                                    <div style={{ display: 'flex', gap: '30px', justifyContent: 'center', marginBottom: '10px' }}>
                                        <span style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                            <span style={{ width: '20px', height: '20px', background: '#00f3ff', borderRadius: '4px' }}></span>
                                            3-Peg Games: <strong>{comparisonData.count3Peg || 0}</strong>
                                        </span>
                                        <span style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                            <span style={{ width: '20px', height: '20px', background: '#00ff9d', borderRadius: '4px' }}></span>
                                            4-Peg Games: <strong>{comparisonData.count4Peg || 0}</strong>
                                        </span>
                                    </div>
                                </div>
                            )}
                        </div>

                        <div className="charts-grid">
                            <div className="glass-panel chart-panel">
                                <h3>Average Execution Times</h3>
                                <div className="chart-container">
                                    {getChartData() && <Bar data={getChartData()} options={chartOptions} />}
                                </div>
                            </div>

                            <div className="glass-panel chart-panel">
                                <h3>Performance Trend</h3>
                                <div className="chart-container">
                                    {getTrendData() && <Line data={getTrendData()} options={chartOptions} />}
                                </div>
                            </div>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}

export default AlgorithmComparison;
