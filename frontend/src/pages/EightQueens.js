import { useEffect, useState } from "react";
import "../App.css";
import "./EightQueens.css";

import Confetti from "../components/Confetti";
import GameHero from "../components/GameHero";
import EightQueensService from "../services/EightQueensService";

function EightQueens() {
  const [step, setStep] = useState(1);
  const [playerName, setPlayerName] = useState("");
  const [queens, setQueens] = useState([]);
  const [submissionResult, setSubmissionResult] = useState(null);
  const [stats, setStats] = useState(null);
  const [error, setError] = useState("");
  const [showConfetti, setShowConfetti] = useState(false);
  const [conflicts, setConflicts] = useState([]);

  useEffect(() => { loadStats(); }, []);
  const loadStats = async () => {
    try { const res = await EightQueensService.getStats(); setStats(res.data); } catch (err) { console.error(err); }
  };

  const handleStartGame = () => {
    if (!playerName) { setError("Name required"); return; }
    setStep(2); setError("");
  };

  const toggleQueen = (r, c) => {
    // Clear conflicts and results on interaction to reset state
    setConflicts([]);
    if (submissionResult) setSubmissionResult(null); // Reset success state on new move

    const exists = queens.find(q => q.r === r && q.c === c);
    if (exists) setQueens(queens.filter(q => q.r !== r || q.c !== c));
    else if (queens.length < 8) setQueens([...queens, { r, c }]);
    else { setError("Max 8 Queens"); setTimeout(() => setError(""), 2000); }
  };

  const isQueenAt = (r, c) => queens.some(q => q.r === r && q.c === c);

  /* Helper to find conflicting queens */
  const calculateConflicts = (currentQueens) => {
    const conflictSet = new Set();
    for (let i = 0; i < currentQueens.length; i++) {
      for (let j = i + 1; j < currentQueens.length; j++) {
        const q1 = currentQueens[i];
        const q2 = currentQueens[j];

        const sameRow = q1.r === q2.r;
        const sameCol = q1.c === q2.c;
        const sameDiag = Math.abs(q1.r - q2.r) === Math.abs(q1.c - q2.c);

        if (sameRow || sameCol || sameDiag) {
          conflictSet.add(`${q1.r}-${q1.c}`);
          conflictSet.add(`${q2.r}-${q2.c}`);
        }
      }
    }
    return Array.from(conflictSet);
  };

  const handleSubmit = async () => {
    if (queens.length !== 8) { setError("Place exactly 8 Queens"); return; }

    // Check for conflicts immediately
    const foundConflicts = calculateConflicts(queens);
    if (foundConflicts.length > 0) {
      setConflicts(foundConflicts);
      setError("Invalid Pattern Detected");
    } else {
      setConflicts([]);
    }

    const sorted = [...queens].sort((a, b) => a.r - b.r);

    try {
      const res = await EightQueensService.submitSolution(playerName, sorted.map(q => q.c));
      setSubmissionResult(res.data); loadStats();
      if (res.data.valid) {
        setShowConfetti(true);
        setConflicts([]);
      } else {
        if (foundConflicts.length === 0) setConflicts(calculateConflicts(queens));
      }
    } catch (err) { setError("Network Error"); }
  };

  const renderBoard = () => {
    const board = [];
    const isSuccess = submissionResult?.valid;

    // Define Colors based on state
    const themeColor = isSuccess ? '#00ff9d' : '#00f3ff'; // Green if success, else Cyan
    const errorColor = '#ff0055'; // Pink

    for (let r = 0; r < 8; r++) {
      const row = [];
      for (let c = 0; c < 8; c++) {
        const isDark = (r + c) % 2 === 1;
        const hasQueen = isQueenAt(r, c);
        const isConflict = conflicts.includes(`${r}-${c}`);

        // Dynamic Styles
        let cellBg = isDark ? 'rgba(0,0,0,0.5)' : `rgba(0, 243, 255, 0.05)`;
        let borderColor = hasQueen ? `2px solid ${themeColor}` : `1px solid rgba(255, 255, 255, 0.1)`;
        let boxShadow = hasQueen ? `0 0 15px ${themeColor}, inset 0 0 10px ${themeColor}` : 'none';
        let queenShadow = `0 0 10px ${themeColor}`;

        // Override for Success
        if (isSuccess) {
          cellBg = isDark ? 'rgba(0, 50, 20, 0.5)' : 'rgba(0, 255, 157, 0.1)';
          if (hasQueen) {
            borderColor = `2px solid ${themeColor}`;
            boxShadow = `0 0 25px ${themeColor}, inset 0 0 15px ${themeColor}`;
          }
        }

        // Override for Conflict (Error takes precedence unless success - though success implies no conflict)
        if (isConflict && hasQueen && !isSuccess) {
          borderColor = `2px solid ${errorColor}`;
          boxShadow = `0 0 15px ${errorColor}, inset 0 0 10px ${errorColor}`;
          queenShadow = `0 0 15px ${errorColor}`;
        }

        row.push(
          <div key={`${r}-${c}`} onClick={() => toggleQueen(r, c)}
            style={{
              width: '45px', height: '45px',
              backgroundColor: cellBg,
              border: borderColor,
              display: 'flex', justifyContent: 'center', alignItems: 'center',
              fontSize: '28px', cursor: 'pointer', color: '#fff',
              boxShadow: boxShadow,
              transition: 'all 0.3s ease',
              transform: isSuccess && hasQueen ? 'scale(1.1)' : 'scale(1)'
            }}>
            {hasQueen ? <span style={{ textShadow: queenShadow }}>♕</span> : ''}
          </div>
        );
      }
      board.push(<div key={r} style={{ display: 'flex' }}>{row}</div>);
    }
    return <div style={{
      border: `2px solid ${isSuccess ? '#00ff9d' : '#00f3ff'}`,
      padding: '5px', borderRadius: '4px',
      background: 'rgba(0,0,0,0.3)',
      boxShadow: `0 0 30px ${isSuccess ? 'rgba(0, 255, 157, 0.3)' : 'rgba(0, 243, 255, 0.1)'}`,
      transition: 'all 0.5s ease'
    }}>{board}</div>;
  };

  return (
    <div className="page">
      <GameHero
        title="Eight Queens"
        subtitle="Backtracking & Constraint Satisfaction"
        icon="/assets/icons/queens.png"
        gradient="var(--gradient-purple)"
        color={submissionResult?.valid ? "#00ff9d" : "#00f3ff"}
      />

      <Confetti active={showConfetti} onComplete={() => setShowConfetti(false)} />


      {step === 1 && (
        <div className="queens-container">
          <div className="queens-split fade-in-up">
            {/* Left Side: Image */}
            <div className="queens-image-side">
              <img src="/assets/images/queens_hero.png" alt="Cyberpunk Chess" className="queens-hero-img" />
              <div className="queens-overlay-text">
                <h2>Tactical Logic</h2>
                <p>Resolve constraints. Master the board.</p>
              </div>
            </div>

            {/* Right Side: Form */}
            <div className="queens-form-side">
              <div className="queens-form-container">
                <h1 className="queens-title">QUEENS PROTOCOL</h1>
                <p className="queens-subtitle">Initialize strategy module.</p>

                <div className="queens-input-group">
                  <label className="queens-input-label">PLAYER NAME</label>
                  <input
                    type="text"
                    className="queens-input"
                    value={playerName}
                    onChange={e => setPlayerName(e.target.value)}
                    placeholder="Enter Name..."
                  />
                </div>

                {error && <p style={{ color: '#ff0055', marginBottom: '15px' }}>{error}</p>}

                <button className="queens-btn-start" onClick={handleStartGame}>
                  PROCESS SIMULATION
                </button>
              </div>
            </div>
          </div>
        </div>
      )}


      {step === 2 && (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '20px', paddingBottom: '40px' }}>

          {stats && (
            <div className="glass-panel" style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.9rem', width: '100%', maxWidth: '600px', borderColor: '#00f3ff' }}>
              <span>Solutions Found: <strong style={{ color: '#00f3ff' }}>{stats.totalDiscoveredByPlayers}</strong> / 92</span>
              <span className="text-muted">Global Stats</span>
            </div>
          )}

          <div className="glass-panel" style={{
            display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '20px',
            borderColor: submissionResult?.valid ? '#00ff9d' : '#00f3ff',
            boxShadow: submissionResult?.valid ? '0 0 50px rgba(0, 255, 157, 0.2)' : '0 0 30px rgba(0, 243, 255, 0.1)',
            transition: 'all 0.5s ease'
          }}>
            {renderBoard()}

            <div style={{ display: 'flex', gap: '10px', width: '100%', maxWidth: '400px' }}>
              <button className="btn-secondary" onClick={() => { setQueens([]); setSubmissionResult(null); }} style={{ flex: 1, borderColor: '#00f3ff', color: '#00f3ff' }}>CLEAR BOARD</button>
              <button className="btn-primary" onClick={handleSubmit} style={{ flex: 1, background: 'linear-gradient(45deg, #00f3ff, #0066ff)', border: 'none', color: '#000' }}>VERIFY PATTERN</button>
            </div>

            {error && <p style={{ color: '#ff0055' }}>{error}</p>}

            {submissionResult && (
              <div className="glass-panel" style={{ width: '100%', borderColor: submissionResult.valid ? '#00ff9d' : '#ff0055' }}>
                <h3 style={{ color: submissionResult.valid ? '#00ff9d' : '#ff0055', textAlign: 'center' }}>{submissionResult.message}</h3>
                {submissionResult.valid && <p style={{ textAlign: 'center', color: '#fff' }}>Perfect Strategy! Configuration Accepted.</p>}
              </div>
            )}
          </div>

          <button className="btn-secondary" onClick={() => setStep(1)} style={{ marginTop: '20px', opacity: 0.7 }}>Return to Config</button>
        </div>
      )}
    </div>
  );
}

export default EightQueens;
