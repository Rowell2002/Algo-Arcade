import { useState } from "react";
import "../App.css";
import "./TowerOfHanoi.css";

import Confetti from "../components/Confetti";
import GameHero from "../components/GameHero";
import HanoiService from "../services/HanoiService";

function TowerOfHanoi() {
  const [step, setStep] = useState(1);
  const [playerName, setPlayerName] = useState("");
  const [numPegs, setNumPegs] = useState(3);
  const [gameData, setGameData] = useState(null);
  const [pegs, setPegs] = useState([]);
  const [selectedPeg, setSelectedPeg] = useState(null);
  const [userMoves, setUserMoves] = useState([]);
  const [moveCount, setMoveCount] = useState(0);
  const [userGuess, setUserGuess] = useState("");
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [showConfetti, setShowConfetti] = useState(false);

  const handleStartGame = async () => {
    if (!playerName) { setError("Name required"); return; }
    try {
      const res = await HanoiService.startGame(playerName, parseInt(numPegs));
      setGameData(res.data);
      const n = res.data.numDisks;
      // Initialize Peg 0 with disks N..1
      const startPeg = [];
      for (let i = n; i >= 1; i--) startPeg.push(i);
      const initOnly = Array.from({ length: res.data.numPegs }, () => []);
      initOnly[0] = startPeg;
      setPegs(initOnly); setUserMoves([]); setMoveCount(0); setSelectedPeg(null); setStep(2);
    } catch (err) { setError("Start failed"); }
  };

  const handlePegClick = (idx) => {
    if (selectedPeg === null) {
      if (pegs[idx].length > 0) setSelectedPeg(idx);
    } else {
      if (selectedPeg === idx) { setSelectedPeg(null); return; }
      const source = [...pegs[selectedPeg]];
      const dest = [...pegs[idx]];
      const disk = source[source.length - 1];
      if (dest.length > 0 && disk > dest[dest.length - 1]) {
        alert("Invalid Move!"); setSelectedPeg(null); return;
      }
      source.pop(); dest.push(disk);
      const newPegs = [...pegs]; newPegs[selectedPeg] = source; newPegs[idx] = dest;
      setPegs(newPegs);
      setMoveCount(moveCount + 1);
      setUserMoves([...userMoves, `${String.fromCharCode(65 + selectedPeg)} -> ${String.fromCharCode(65 + idx)} `]);
      setSelectedPeg(null);
    }
  };

  const handleSubmit = async () => {
    if (!userGuess) return;
    try {
      const res = await HanoiService.solveGame(gameData.gameId, parseInt(userGuess), userMoves);
      setResult(res.data); setStep(3);
      if (res.data.correct) {
        setShowConfetti(true);
      }
    } catch (err) { setError("Submit failed"); }
  };

  const handleRestart = () => { setStep(1); setGameData(null); setResult(null); setUserGuess(""); };

  const renderPegs = () => {
    return (
      <div className="hanoi-board">
        {pegs.map((peg, idx) => (
          <div key={idx} onClick={() => handlePegClick(idx)}
            className={`hanoi-peg-container ${selectedPeg === idx ? 'selected' : ''}`}>

            <div className="hanoi-peg-pole"></div>
            <div className="hanoi-peg-base"></div>

            <div style={{ display: 'flex', flexDirection: 'column-reverse', height: '100%', alignItems: 'center', paddingBottom: '10px', position: 'relative', zIndex: 2 }}>
              {peg.map((disk, i) => (
                <div key={i} style={{
                  width: `${30 + disk * 15}px`, height: '20px', marginBottom: '2px', borderRadius: '4px',
                  background: `linear-gradient(90deg, #b026ff, #4b0082)`,
                  opacity: 0.9,
                  boxShadow: '0 0 10px rgba(176, 38, 255, 0.5)',
                  border: '1px solid rgba(255,255,255,0.3)',
                  zIndex: 2
                }}></div>
              ))}
            </div>
            <div className="hanoi-label">{String.fromCharCode(65 + idx)}</div>
          </div>
        ))}
      </div>
    );
  };

  return (
    <div className="page">
      <GameHero
        title="Tower of Hanoi"
        subtitle="Recursive Puzzle Solving Challenge"
        icon="/assets/icons/hanoi.png"
        gradient="var(--gradient-green)"
        color="#b026ff"
      />

      <Confetti active={showConfetti} onComplete={() => setShowConfetti(false)} />


      {step === 1 && (
        <div className="hanoi-container">
          <div className="hanoi-split fade-in-up">
            {/* Left Side: Image */}
            <div className="hanoi-image-side">
              <img src="/assets/images/hanoi_hero.png" alt="Cyberpunk Towers" className="hanoi-hero-img" />
              <div className="hanoi-overlay-text">
                <h2>Recursive Logic</h2>
                <p>Move the stack. Maintain the order.</p>
              </div>
            </div>

            {/* Right Side: Form */}
            <div className="hanoi-form-side">
              <div className="hanoi-form-container">
                <h1 className="hanoi-title">HANOI LINK</h1>
                <p className="hanoi-subtitle">Initiate puzzle sequence.</p>

                <div className="hanoi-input-group">
                  <label className="hanoi-input-label">PLAYER NAME</label>
                  <input
                    type="text"
                    className="hanoi-input"
                    value={playerName}
                    onChange={e => setPlayerName(e.target.value)}
                    placeholder="Enter Name..."
                  />
                </div>

                <div className="hanoi-input-group">
                  <label className="hanoi-input-label">CONFIGURATION</label>
                  <select className="hanoi-select" value={numPegs} onChange={e => setNumPegs(e.target.value)}>
                    <option value="3">3 Pegs (Standard)</option>
                    <option value="4">4 Pegs (Advanced)</option>
                  </select>
                </div>

                {error && <p style={{ color: '#ff0055', marginBottom: '15px' }}>{error}</p>}

                <button className="hanoi-btn-start" onClick={handleStartGame}>
                  INITIALIZE PUZZLE
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {step === 2 && gameData && (
        <div className="game-section">
          <div className="glass-panel" style={{ display: 'flex', justifyContent: 'space-between', borderColor: '#b026ff' }}>
            <span>Target: Stack all on Peg {String.fromCharCode(65 + gameData.numPegs - 1)}</span>
            <span>Moves: <strong style={{ color: '#b026ff' }}>{moveCount}</strong></span>
          </div>

          <div className="glass-panel" style={{ borderColor: '#b026ff' }}>{renderPegs()}</div>

          <div className="glass-panel" style={{ display: 'flex', gap: '10px', borderColor: '#b026ff' }}>
            <input
              type="number"
              value={userGuess}
              onChange={e => setUserGuess(e.target.value)}
              placeholder="Min Moves Guess"
              style={{ flex: 1, padding: '10px', background: 'rgba(0,0,0,0.5)', border: '1px solid #b026ff', color: 'white', borderRadius: '5px' }}
            />
            <button className="btn-secondary" onClick={handleSubmit} style={{ borderColor: '#b026ff', color: '#b026ff' }}>TERMINATE</button>
          </div>
        </div>
      )}

      {step === 3 && result && (
        <div className="glass-panel" style={{ borderColor: result.correct ? '#00ff9d' : '#ff0055' }}>
          <h3 style={{ textAlign: 'center', color: result.correct ? '#00ff9d' : '#ff0055', fontSize: '2rem' }}>{result.message}</h3>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', textAlign: 'center', margin: '20px 0' }}>
            <div>
              <p className="text-muted">Your Estimate</p>
              <h2 style={{ color: result.correct ? '#00ff9d' : 'white' }}>{result.userMinMoves}</h2>
            </div>
            <div>
              <p className="text-muted">Optimal Moves</p>
              <h2 style={{ color: '#b026ff' }}>{result.optimalMinMoves}</h2>
            </div>
          </div>

          <h4 className="text-muted">Algorithm Benchmarks</h4>
          <ul>
            {Object.entries(result.algorithmTimes).map(([algo, time]) => (
              <li key={algo} style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.1)', padding: '10px' }}>
                <span>{algo}</span> <span style={{ color: '#b026ff', fontFamily: 'monospace' }}>{time} μs</span>
              </li>
            ))}
          </ul>
          <button className="btn-primary" onClick={handleRestart} style={{ width: '100%', marginTop: '20px', background: 'linear-gradient(45deg, #b026ff, #4b0082)', border: 'none' }}>ACKNOWLEDGE</button>
        </div>
      )}
    </div>
  );
}

export default TowerOfHanoi;
