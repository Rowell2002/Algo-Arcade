import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../App.css";
import "./TrafficSimulation.css";
import "../components/GameSelector.css";

import Confetti from "../components/Confetti";
import GameHero from "../components/GameHero";
import TrafficService from "../services/TrafficService";

const GAMES = [
  { id: 'snakeladder', name: 'Snake & Ladder', icon: '/assets/icons/snake-ladder.png', route: '/snake-ladder' },
  { id: 'traffic', name: 'Traffic Simulation', icon: '/assets/icons/traffic.png', route: '/traffic' },
  { id: 'tsp', name: 'Traveling Salesman', icon: '/assets/icons/tsp.png', route: '/tsp' },
  { id: 'hanoi', name: 'Tower of Hanoi', icon: '/assets/icons/hanoi.png', route: '/hanoi' },
  { id: 'eight-queens', name: 'Eight Queens', icon: '/assets/icons/queens.png', route: '/eight-queens' }
];

function TrafficSimulation() {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [playerName, setPlayerName] = useState("");
  const [gameData, setGameData] = useState(null);
  const [userGuess, setUserGuess] = useState("");
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [showConfetti, setShowConfetti] = useState(false);

  const handleGameSelect = (route) => {
    navigate(route);
  };

  const handleStartGame = async () => {
    if (!playerName) { setError("Please enter your name"); return; }
    try {
      setError("");
      const response = await TrafficService.startGame(playerName);
      setGameData(response.data);
      setStep(2);
    } catch (err) {
      console.error(err);
      setError("Failed to start game.");
    }
  };

  const handleSubmitGuess = async () => {
    if (!userGuess) return;
    try {
      const response = await TrafficService.solveGame(gameData.gameId, parseInt(userGuess));
      setResult(response.data);
      setStep(3);
      if (response.data.correct) {
        setShowConfetti(true);
      }
    } catch (err) {
      console.error(err);
      setError("Failed to submit.");
    }
  };

  const handleRestart = () => {
    setStep(1); setGameData(null); setResult(null); setUserGuess(""); setError("");
  };

  const nodePositions = {
    'A': { x: 50, y: 150, label: 'A' },
    'B': { x: 200, y: 50, label: 'B' },
    'C': { x: 200, y: 150, label: 'C' },
    'D': { x: 200, y: 250, label: 'D' },
    'E': { x: 350, y: 100, label: 'E' },
    'F': { x: 350, y: 200, label: 'F' },
    'G': { x: 500, y: 100, label: 'G' },
    'H': { x: 500, y: 200, label: 'H' },
    'T': { x: 650, y: 150, label: 'T' }
  };

  const renderGraph = () => {
    if (!gameData) return null;
    return (
      <div className="graph-container" style={{ background: 'rgba(0,0,0,0.5)', borderColor: '#ff0055', boxShadow: '0 0 20px rgba(255, 0, 85, 0.2)' }}>
        <svg width="100%" height="300" viewBox="0 0 700 300">
          <defs>
            <marker id="arrow" markerWidth="10" markerHeight="10" refX="28" refY="3" orient="auto" markerUnits="strokeWidth">
              <path d="M0,0 L0,6 L9,3 z" fill="rgba(255,255,255,0.6)" />
            </marker>
          </defs>
          {gameData.edges.map((edge, idx) => {
            const start = nodePositions[edge.from];
            const end = nodePositions[edge.to];
            const midX = (start.x + end.x) / 2;
            const midY = (start.y + end.y) / 2;
            return (
              <g key={idx}>
                <line x1={start.x} y1={start.y} x2={end.x} y2={end.y} stroke="rgba(255,255,255,0.3)" strokeWidth="2" markerEnd="url(#arrow)" />
                <rect x={midX - 12} y={midY - 12} width="24" height="24" rx="4" fill="#111" stroke="#ff0055" strokeWidth="1" />
                <text x={midX} y={midY + 5} textAnchor="middle" fill="#ff0055" fontSize="12" fontWeight="bold">{edge.capacity}</text>
              </g>
            );
          })}
          {Object.keys(nodePositions).map(key => {
            const pos = nodePositions[key];
            const isSourceSink = key === 'A' || key === 'T';
            return (
              <g key={key}>
                <circle
                  cx={pos.x} cy={pos.y} r="18"
                  fill={isSourceSink ? '#ff0055' : '#111'}
                  stroke={isSourceSink ? '#fff' : '#ff0055'}
                  strokeWidth="2"
                  style={{ filter: isSourceSink ? 'drop-shadow(0 0 5px #ff0055)' : '' }}
                />
                <text x={pos.x} y={pos.y + 5} textAnchor="middle" fill="#fff" fontWeight="bold">{pos.label}</text>
              </g>
            );
          })}
        </svg>
      </div>
    );
  };

  return (
    <div className="page">

      <GameHero
        title="Traffic Simulation"
        subtitle="Max-Flow Min-Cut Network Optimization"
        icon="/assets/icons/traffic.png"
        gradient="var(--gradient-pink)"
        color="#ff0055"
      />

      <Confetti active={showConfetti} onComplete={() => setShowConfetti(false)} />


      {step === 1 && (
        <div className="ts-container">
          <div className="ts-split fade-in-up">
            {/* Left Side: Image */}
            <div className="ts-image-side">
              <img src="/assets/images/traffic_hero.png" alt="Cyberpunk Traffic" className="ts-hero-img" />
              <div className="ts-overlay-text">
                <h2>Flow Control</h2>
                <p>Analyze the network. Optimize the stream.</p>
              </div>
            </div>

            {/* Right Side: Form */}
            <div className="ts-form-side">
              <div className="ts-form-container">
                <h1 className="ts-title">TRAFFIC SIM</h1>
                <p className="ts-subtitle">Establish connection signal.</p>

                <div className="ts-input-group">
                  <label className="ts-input-label">PLAYER NAME</label>
                  <input
                    type="text"
                    className="ts-input"
                    value={playerName}
                    onChange={e => setPlayerName(e.target.value)}
                    placeholder="Enter Name..."
                  />
                </div>

                {error && <p style={{ color: '#ff0055', marginBottom: '15px' }}>{error}</p>}

                <button className="ts-btn-start" onClick={handleStartGame}>
                  CONNECT TO GRID
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {step === 2 && gameData && (
        <div className="game-section">
          <div className="glass-panel">
            <p style={{ color: 'var(--text-muted)' }}>Calculate Max Flow from <strong>Node A</strong> to <strong>Node T</strong>.</p>
          </div>
          {renderGraph()}
          <div className="glass-panel" style={{ marginTop: '20px', textAlign: 'center' }}>
            <label>Estimated Max Flow: </label>
            <input type="number" value={userGuess} onChange={e => setUserGuess(e.target.value)} style={{ width: '80px', marginRight: '10px', padding: '10px', borderRadius: '5px', border: '1px solid #ff0055', background: 'rgba(0,0,0,0.5)', color: 'white' }} />
            <button className="btn-primary" onClick={handleSubmitGuess} style={{ background: 'linear-gradient(45deg, #ff0055, #ff00cc)', border: 'none' }}>Verify Flow</button>
          </div>
        </div>
      )}

      {step === 3 && result && (
        <div className="glass-panel" style={{ textAlign: 'center', borderColor: result.correct ? '#00ff9d' : '#ff0055' }}>
          <h3 style={{ color: result.correct ? '#00ff9d' : '#ff0055', fontSize: '2rem' }}>{result.message}</h3>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', margin: '20px 0', textAlign: 'center' }}>
            <div>
              <p className="text-muted">Your Estimate</p>
              <h2 style={{ color: result.correct ? '#00ff9d' : 'white' }}>{result.userGuess}</h2>
            </div>
            <div>
              <p className="text-muted">Actual Max Flow</p>
              <h2 style={{ color: '#ff0055' }}>{result.maxFlow}</h2>
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-around', marginTop: '20px', flexWrap: 'wrap', gap: '15px' }}>
            <div className="stat-card" style={{ borderColor: '#ff0055' }}>
              <small>Ford-Fulkerson</small>
              <p style={{ color: '#ff0055' }}>{result.algorithmTimes.FordFulkerson} μs</p>
            </div>
            <div className="stat-card" style={{ borderColor: '#ff00cc' }}>
              <small>Edmonds-Karp</small>
              <p style={{ color: '#ff00cc' }}>{result.algorithmTimes.EdmondsKarp} μs</p>
            </div>
          </div>
          <button className="btn-secondary" onClick={handleRestart} style={{ marginTop: '20px' }}>New Simulation</button>
        </div>
      )}
    </div>
  );
}

export default TrafficSimulation;
