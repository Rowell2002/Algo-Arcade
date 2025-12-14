import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../App.css";
import "./TravelingSalesman.css";

import Confetti from "../components/Confetti";
import GameHero from "../components/GameHero";
import TspService from "../services/TspService";

const ALL_CITIES = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];

const GAMES = [
  { id: 'snakeladder', name: 'Snake & Ladder', icon: '/assets/icons/snake-ladder.png', route: '/snake-ladder' },
  { id: 'traffic', name: 'Traffic Simulation', icon: '/assets/icons/traffic.png', route: '/traffic' },
  { id: 'tsp', name: 'Traveling Salesman', icon: '/assets/icons/tsp.png', route: '/tsp' },
  { id: 'hanoi', name: 'Tower of Hanoi', icon: '/assets/icons/hanoi.png', route: '/hanoi' },
  { id: 'eight-queens', name: 'Eight Queens', icon: '/assets/icons/queens.png', route: '/eight-queens' }
];

function TravelingSalesman() {
  const navigate = useNavigate();
  const [playerName, setPlayerName] = useState("");
  const [gameData, setGameData] = useState(null);
  const [selectedCities, setSelectedCities] = useState([]);
  const [path, setPath] = useState([]);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [showConfetti, setShowConfetti] = useState(false);

  const handleGameSelect = (route) => {
    navigate(route);
  };

  const handleStart = async () => {
    if (!playerName) { setError("Enter Name"); return; }
    try {
      const res = await TspService.startGame(playerName);
      setGameData(res.data);
      setSelectedCities([]); setPath([]); setResult(null); setError("");
    } catch (err) {
      console.error(err);
      setError("Failed to start game");
    }
  };

  const toggleCitySelection = (city) => {
    if (city === gameData.homeCity) return;
    if (selectedCities.includes(city)) {
      setSelectedCities(selectedCities.filter(c => c !== city));
      setPath(path.filter(c => c !== city));
    } else {
      setSelectedCities([...selectedCities, city]);
    }
  };

  const addToPath = (city) => { if (!path.includes(city)) setPath([...path, city]); };
  const removeFromPath = (city) => { setPath(path.filter(c => c !== city)); };

  const handleSubmit = async () => {
    if (path.length !== selectedCities.length) { setError(`Select cities visited: ${path.length}/${selectedCities.length}`); return; }
    try {
      const res = await TspService.solveGame(gameData.gameId, path);
      setResult(res.data);
      if (res.data.correct) {
        setShowConfetti(true);
      }
    } catch (err) {
      console.error(err);
      setError("Submission Failed");
    }
  };

  const getDistance = (c1, c2) => gameData ? gameData.distances[c1][c2] : 0;

  const calculateCurrentPathDist = () => {
    if (!gameData || path.length === 0) return 0;
    let dist = 0;
    let curr = gameData.homeCity;
    for (let next of path) { dist += getDistance(curr, next); curr = next; }
    dist += getDistance(curr, gameData.homeCity);
    return dist;
  };

  return (
    <div className="page">
      <GameHero
        title="Traveling Salesman"
        subtitle="NP-Hard Optimization Challenge"
        icon="/assets/icons/tsp.png"
        gradient="var(--gradient-sunset)"
        color="#ffd700"
      />

      <Confetti active={showConfetti} onComplete={() => setShowConfetti(false)} />


      {!gameData && (
        <div className="tsp-container">
          <div className="tsp-split fade-in-up">
            {/* Left Side: Image */}
            <div className="tsp-image-side">
              <img src="/assets/images/tsp_hero.png" alt="Cyberpunk City Map" className="tsp-hero-img" />
              <div className="tsp-overlay-text">
                <h2>Global Logistics</h2>
                <p>Optimize the route. Connect the nodes.</p>
              </div>
            </div>

            {/* Right Side: Form */}
            <div className="tsp-form-side">
              <div className="tsp-form-container">
                <h1 className="tsp-title">TSP SYSTEM</h1>
                <p className="tsp-subtitle">Initialize pathfinding module.</p>

                <div className="tsp-input-group">
                  <label className="tsp-input-label">PLAYER NAME</label>
                  <input
                    type="text"
                    className="tsp-input"
                    value={playerName}
                    onChange={e => setPlayerName(e.target.value)}
                    placeholder="Enter Name..."
                  />
                </div>

                {error && <p style={{ color: '#ff0055', marginBottom: '15px' }}>{error}</p>}

                <button className="tsp-btn-start" onClick={handleStart}>
                  START ROUTE PLAN
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {gameData && (
        <div className="game-layout" style={{ display: 'flex', gap: '20px', flexWrap: 'wrap', paddingBottom: '40px' }}>

          {/* Left: Selection */}
          <div className="glass-panel" style={{ flex: 1, minWidth: '300px', borderColor: '#ffd700' }}>
            <h3 style={{ color: '#ffd700' }}>1. Sector Selection</h3>
            <p className="text-muted">Home Base: <strong style={{ color: '#fff' }}>{gameData.homeCity}</strong></p>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: '10px', marginTop: '20px' }}>
              {ALL_CITIES.map(c => {
                const isHome = c === gameData.homeCity;
                const isSelected = selectedCities.includes(c);
                return (
                  <button
                    key={c} disabled={isHome} onClick={() => toggleCitySelection(c)}
                    style={{
                      borderColor: isHome ? '#fff' : (isSelected ? '#ffd700' : '#444'),
                      color: isHome ? '#000' : (isSelected ? '#ffd700' : '#888'),
                      background: isHome ? '#ffd700' : 'transparent',
                      opacity: (isHome || isSelected) ? 1 : 0.7,
                      padding: '10px',
                      borderRadius: '5px',
                      cursor: isHome ? 'default' : 'pointer'
                    }}
                  >{c}</button>
                );
              })}
            </div>

            <div style={{ marginTop: '20px', maxHeight: '200px', overflowY: 'auto' }}>
              <h4>Distances</h4>
              <table style={{ fontSize: '0.8rem', width: '100%' }}>
                <tbody>
                  {ALL_CITIES.filter(c => c !== gameData.homeCity).map(c => (
                    <tr key={c}>
                      <td style={{ padding: '4px' }}>{gameData.homeCity} ↔ {c}</td>
                      <td style={{ textAlign: 'right', padding: '4px', color: '#ffd700' }}>{getDistance(gameData.homeCity, c)} km</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Right: Path */}
          <div className="glass-panel" style={{ flex: 1, minWidth: '300px', borderColor: '#ffd700' }}>
            <h3 style={{ color: '#ffd700' }}>2. Flight Path</h3>
            <p className="text-muted">Click targets to add to route.</p>

            <div style={{ display: 'flex', gap: '5px', flexWrap: 'wrap', marginBottom: '20px' }}>
              {selectedCities.filter(c => !path.includes(c)).map(c => (
                <button key={c} onClick={() => addToPath(c)} className="btn-secondary" style={{ padding: '5px 10px', fontSize: '0.8rem', borderColor: '#ffae00', color: '#ffae00' }}>+ {c}</button>
              ))}
            </div>

            <div style={{ padding: '15px', background: 'rgba(0,0,0,0.4)', borderRadius: '8px', fontFamily: 'monospace', border: '1px solid rgba(255, 215, 0, 0.3)' }}>
              <span style={{ color: '#ffae00' }}>{gameData.homeCity}</span>
              {path.map(c => (
                <span key={c}> → <span onClick={() => removeFromPath(c)} style={{ color: '#ffd700', cursor: 'pointer', textDecoration: 'underline' }}>{c}</span></span>
              ))}
              <span style={{ color: '#ffae00' }}> → {gameData.homeCity}</span>
            </div>

            <p style={{ marginTop: '10px', fontSize: '1.2rem' }}>Total: <strong style={{ color: '#ffd700' }}>{calculateCurrentPathDist()} km</strong></p>

            <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
              <button className="btn-primary" onClick={handleSubmit} disabled={path.length === 0} style={{ flex: 1, background: 'linear-gradient(45deg, #ffd700, #ffae00)', color: '#000', border: 'none' }}>EXECUTE</button>
              <button className="btn-secondary" onClick={() => { setPath([]); setSelectedCities([]); setGameData(null) }} style={{ flex: 1 }}>ABORT</button>
            </div>
            {error && <p style={{ color: '#ff0055' }}>{error}</p>}
          </div>
        </div>
      )}

      {result && (
        <div className="glass-panel" style={{ marginTop: '20px', borderColor: result.correct ? '#00ff9d' : '#ff0055' }}>
          <h3 style={{ textAlign: 'center', color: result.correct ? '#00ff9d' : '#ff0055', fontSize: '2rem' }}>{result.message}</h3>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', textAlign: 'center', margin: '20px 0' }}>
            <div>
              <p className="text-muted">Your Distance</p>
              <h2 style={{ color: result.correct ? '#00ff9d' : 'white' }}>{result.userDistance} km</h2>
            </div>
            <div>
              <p className="text-muted">Optimal (Brute Force)</p>
              <h2 style={{ color: '#ffd700' }}>{result.minDistance} km</h2>
            </div>
          </div>

          {/* Optimal Path Display */}
          <div style={{
            background: 'rgba(0, 255, 157, 0.1)',
            border: '1px solid #00ff9d',
            borderRadius: '8px',
            padding: '15px',
            margin: '20px 0',
            textAlign: 'center'
          }}>
            <p className="text-muted" style={{ marginBottom: '10px' }}>Optimal Route</p>
            <h3 style={{
              color: '#00ff9d',
              fontFamily: 'Orbitron, sans-serif',
              letterSpacing: '2px'
            }}>
              {result.optimalPath ? result.optimalPath.join(' → ') : 'N/A'}
            </h3>
          </div>

          <h4>Algorithm Telemetry</h4>
          <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
            <thead>
              <tr>
                <th style={{ textAlign: 'center', padding: '10px', color: '#ffd700' }}>Algorithm</th>
                <th style={{ textAlign: 'center', padding: '10px', color: '#ffd700' }}>Time (ns)</th>
              </tr>
            </thead>
            <tbody>
              {Object.keys(result.algorithmTimes).map(algo => (
                <tr key={algo} style={{ borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
                  <td style={{ textAlign: 'center', padding: '10px' }}>{algo}</td>
                  <td style={{ textAlign: 'center', padding: '10px', fontFamily: 'monospace' }}>{result.algorithmTimes[algo]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default TravelingSalesman;
