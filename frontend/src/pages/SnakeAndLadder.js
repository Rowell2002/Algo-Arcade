import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../App.css";
import "./SnakeAndLadder.css";
import "../components/GameSelector.css";

import Confetti from "../components/Confetti";
import GameHero from "../components/GameHero";
import SnakeLadderService from "../services/SnakeLadderService";

const GAMES = [
  { id: 'snakeladder', name: 'Snake & Ladder', icon: '/assets/icons/snake-ladder.png', route: '/snake-ladder' },
  { id: 'traffic', name: 'Traffic Simulation', icon: '/assets/icons/traffic.png', route: '/traffic' },
  { id: 'tsp', name: 'Traveling Salesman', icon: '/assets/icons/tsp.png', route: '/tsp' },
  { id: 'hanoi', name: 'Tower of Hanoi', icon: '/assets/icons/hanoi.png', route: '/hanoi' },
  { id: 'eight-queens', name: 'Eight Queens', icon: '/assets/icons/queens.png', route: '/eight-queens' }
];

function SnakeAndLadder() {
  const navigate = useNavigate();
  const [step, setStep] = useState(0); // 0: Menu, 1: Config, 2: Play, 3: Result
  const [playerName, setPlayerName] = useState("");
  const [boardSize, setBoardSize] = useState(10);
  const [gameData, setGameData] = useState(null);
  const [userGuess, setUserGuess] = useState("");
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [showConfetti, setShowConfetti] = useState(false);
  const [leaderboardData, setLeaderboardData] = useState(null);
  const [showLeaderboard, setShowLeaderboard] = useState(false);

  const handleGameSelect = (route) => {
    navigate(route);
  };

  const handleStartGame = async () => {
    if (!playerName.trim()) {
      setError("Please enter your name");
      return;
    }
    if (boardSize < 6 || boardSize > 12) {
      setError("Board size must be between 6 and 12");
      return;
    }
    try {
      setError("");
      const response = await SnakeLadderService.startGame(playerName.trim(), parseInt(boardSize));
      setGameData(response.data);
      setStep(2);
    } catch (err) {
      console.error(err);
      setError("Failed to start game.");
    }
  };

  const handleSubmitGuess = async (selectedChoice) => {
    const guess = selectedChoice !== undefined ? selectedChoice : parseInt(userGuess);
    if (!guess) return;
    try {
      const response = await SnakeLadderService.solveGame(gameData.gameId, guess);
      setResult(response.data);
      if (response.data.correct) {
        setShowConfetti(true);
      }
      // Result is saved automatically in backend (like Traffic game)
      setStep(3); // Go to result screen
    } catch (err) {
      console.error(err);
      setError("Failed to submit answer.");
    }
  };


  const handleViewLeaderboard = async () => {
    try {
      const response = await SnakeLadderService.getLeaderboard();
      setLeaderboardData(response.data);
      setShowLeaderboard(true);
    } catch (err) {
      console.error(err);
      setError("Failed to load leaderboard.");
    }
  };

  const handleBackToMenu = () => {
    setStep(0);
    setShowLeaderboard(false);
    setGameData(null);
    setResult(null);
    setUserGuess("");
    setError("");
    setPlayerName("");
  };

  const handleRestart = () => {
    setStep(1);
    setGameData(null);
    setResult(null);
    setUserGuess("");
    setError("");
    setPlayerName("");
  };

  // Helper: Get cell center position (as percentage) based on cell number
  const getCellCenter = (cellNum, n) => {
    // Cell numbering: 1 is bottom-left, n*n is top-right/left depending on n
    const row = Math.floor((cellNum - 1) / n); // 0 = bottom row
    const colInRow = (cellNum - 1) % n;

    // Zigzag: even rows go left-to-right, odd rows go right-to-left
    const col = row % 2 === 0 ? colInRow : (n - 1 - colInRow);

    // Convert to percentage (center of cell)
    const x = (col + 0.5) / n * 100;
    const y = (1 - (row + 0.5) / n) * 100; // Invert Y because SVG origin is top-left

    return { x, y };
  };

  const renderBoard = () => {
    if (!gameData) return null;
    const n = gameData.boardSize;
    const gridBoard = [];

    // Generate valid zigzag board order
    for (let r = n - 1; r >= 0; r--) {
      let rowNums = [];
      for (let c = 0; c < n; c++) {
        rowNums.push(r * n + c + 1);
      }
      if (r % 2 !== 0) rowNums.reverse();
      gridBoard.push(...rowNums);
    }

    // Generate arrow paths for snakes and ladders
    const arrows = [];

    // Ladders (green arrows)
    Object.entries(gameData.ladders).forEach(([start, end]) => {
      const from = getCellCenter(parseInt(start), n);
      const to = getCellCenter(parseInt(end), n);
      arrows.push({ from, to, type: 'ladder', key: `ladder-${start}-${end}` });
    });

    // Snakes (red arrows)
    Object.entries(gameData.snakes).forEach(([start, end]) => {
      const from = getCellCenter(parseInt(start), n);
      const to = getCellCenter(parseInt(end), n);
      arrows.push({ from, to, type: 'snake', key: `snake-${start}-${end}` });
    });

    return (
      <div style={{
        position: 'relative',
        width: '100%',
        maxWidth: '600px',
        margin: '20px auto',
      }}>
        {/* Grid Board */}
        <div className="sl-board-grid" style={{ gridTemplateColumns: `repeat(${n}, 1fr)` }}>
          {gridBoard.map(num => {
            let content = num;
            let cellClasses = "sl-cell";

            if (gameData.snakes[num]) {
              cellClasses += " snake-start";
              content = <span title={`Snake to ${gameData.snakes[num]}`}>🐍 {num}</span>;
            }
            if (gameData.ladders[num]) {
              cellClasses += " ladder-start";
              content = <span title={`Ladder to ${gameData.ladders[num]}`}>🪜 {num}</span>;
            }

            // Start & End Indicators
            if (num === 1) {
              cellClasses += " start-cell";
            }
            if (num === n * n) {
              cellClasses += " end-cell";
              content = <span>🏁 {num}</span>;
            }

            return <div key={num} className={cellClasses}>{content}</div>;
          })}
        </div>

        {/* SVG Arrow Overlay */}
        <svg
          style={{
            position: 'absolute',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            pointerEvents: 'none',
            zIndex: 10
          }}
          viewBox="0 0 100 100"
          preserveAspectRatio="none"
        >
          <defs>
            <marker id="arrowhead-green" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
              <polygon points="0 0, 10 3.5, 0 7" fill="#00ff9d" />
            </marker>
            <marker id="arrowhead-red" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
              <polygon points="0 0, 10 3.5, 0 7" fill="#ff0055" />
            </marker>
          </defs>

          {arrows.map(arrow => {
            const isLadder = arrow.type === 'ladder';
            const color = isLadder ? '#00ff9d' : '#ff0055';
            const markerId = isLadder ? 'arrowhead-green' : 'arrowhead-red';

            // Calculate control point for curved path
            const midX = (arrow.from.x + arrow.to.x) / 2;
            const midY = (arrow.from.y + arrow.to.y) / 2;
            const offsetX = (arrow.to.y - arrow.from.y) * 0.2; // Perpendicular offset for curve

            return (
              <path
                key={arrow.key}
                d={`M ${arrow.from.x} ${arrow.from.y} Q ${midX + offsetX} ${midY} ${arrow.to.x} ${arrow.to.y}`}
                stroke={color}
                strokeWidth="0.5"
                fill="none"
                opacity="0.8"
                markerEnd={`url(#${markerId})`}
              />
            );
          })}
        </svg>
      </div>
    );
  };

  return (
    <div className="page">
      <GameHero
        title="Snake & Ladder"
        subtitle="BFS & Dijkstra Pathfinding Challenge"
        icon="/assets/icons/snake-ladder.png"
        gradient="var(--gradient-blue)"
        color="var(--primary-neon)"
      />

      <Confetti active={showConfetti} onComplete={() => setShowConfetti(false)} />

      {/* Step 0: Menu */}
      {step === 0 && !showLeaderboard && (
        <div className="sl-container">
          <div className="sl-menu fade-in-up">
            <div className="glass-panel" style={{ maxWidth: '600px', margin: '50px auto', padding: '60px 40px', textAlign: 'center' }}>
              <h1 style={{ fontSize: '2.5rem', marginBottom: '20px', color: 'var(--primary-neon)' }}>
                SNAKE & LADDER
              </h1>
              <p style={{ color: 'var(--text-muted)', marginBottom: '40px', fontSize: '1.1rem' }}>
                Test your pathfinding skills with BFS and Dijkstra algorithms
              </p>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '20px', maxWidth: '400px', margin: '0 auto' }}>
                <button
                  className="btn-primary"
                  onClick={() => setStep(1)}
                  style={{ padding: '20px', fontSize: '1.2rem' }}
                >
                  🎮 INITIALIZE GAME
                </button>

                <button
                  className="btn-secondary"
                  onClick={handleViewLeaderboard}
                  style={{ padding: '20px', fontSize: '1.2rem' }}
                >
                  🏆 VIEW LEADERBOARD
                </button>
              </div>

              {error && <p style={{ color: 'var(--error-neon)', marginTop: '20px' }}>{error}</p>}
            </div>
          </div>
        </div>
      )}

      {/* Leaderboard View */}
      {showLeaderboard && leaderboardData && (
        <div className="sl-container">
          <div className="glass-panel fade-in-up" style={{ maxWidth: '800px', margin: '50px auto', padding: '40px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
              <h1 style={{ fontSize: '2.5rem', color: 'var(--primary-neon)', margin: 0 }}>
                🏆 LEADERBOARD
              </h1>
              <button className="btn-secondary" onClick={handleBackToMenu}>
                ← Back to Menu
              </button>
            </div>

            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr style={{ borderBottom: '2px solid var(--primary-neon)' }}>
                    <th style={{ padding: '15px', textAlign: 'left', color: 'var(--primary-neon)' }}>Rank</th>
                    <th style={{ padding: '15px', textAlign: 'left', color: 'var(--primary-neon)' }}>Player</th>
                    <th style={{ padding: '15px', textAlign: 'center', color: 'var(--primary-neon)' }}>Board Size</th>
                    <th style={{ padding: '15px', textAlign: 'center', color: 'var(--primary-neon)' }}>Min Throws</th>
                    <th style={{ padding: '15px', textAlign: 'center', color: 'var(--primary-neon)' }}>BFS Time (μs)</th>
                    <th style={{ padding: '15px', textAlign: 'center', color: 'var(--primary-neon)' }}>Date</th>
                  </tr>
                </thead>
                <tbody>
                  {leaderboardData.map((entry, index) => (
                    <tr
                      key={index}
                      style={{
                        borderBottom: '1px solid rgba(0, 243, 255, 0.2)',
                        background: index < 3 ? 'rgba(255, 215, 0, 0.1)' : 'transparent'
                      }}
                    >
                      <td style={{ padding: '15px' }}>
                        {index === 0 && '🥇'}
                        {index === 1 && '🥈'}
                        {index === 2 && '🥉'}
                        {index > 2 && `#${index + 1}`}
                      </td>
                      <td style={{ padding: '15px', color: 'var(--text-primary)', fontWeight: 'bold' }}>
                        {entry.playerName}
                      </td>
                      <td style={{ padding: '15px', textAlign: 'center', color: 'var(--text-muted)' }}>
                        {entry.boardSize}x{entry.boardSize}
                      </td>
                      <td style={{ padding: '15px', textAlign: 'center', color: 'var(--success-neon)' }}>
                        {entry.minDiceThrows}
                      </td>
                      <td style={{ padding: '15px', textAlign: 'center', color: 'var(--accent-neon)' }}>
                        {entry.bfsTime}
                      </td>
                      <td style={{ padding: '15px', textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                        {new Date(entry.createdAt).toLocaleDateString()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {leaderboardData.length === 0 && (
              <p style={{ textAlign: 'center', color: 'var(--text-muted)', marginTop: '40px', fontSize: '1.2rem' }}>
                No results yet. Be the first to play!
              </p>
            )}
          </div>
        </div>
      )}

      {step === 1 && !showLeaderboard && (
        <div className="sl-container">
          <div className="sl-split fade-in-up">

            {/* Left Side: Cyberpunk Image */}
            <div className="sl-image-side">
              <img src="/assets/images/snake_ladder_hero.png" alt="Cyberpunk Snake Ladder" className="sl-hero-img" />
              <div className="sl-overlay-text">
                <h2>Neon Strategy</h2>
                <p>Navigate the grid. Outsmart the algorithm.</p>
              </div>
            </div>

            {/* Right Side: Form */}
            <div className="sl-form-side">
              <div className="sl-form-container">
                <h1 className="sl-title">SNAKE & LADDER</h1>
                <p className="sl-subtitle">Enter your name and select board size.</p>

                <div className="input-group">
                  <label className="input-label">PLAYER NAME</label>
                  <input
                    type="text"
                    className="input-field"
                    placeholder="Enter your name"
                    value={playerName}
                    onChange={e => setPlayerName(e.target.value)}
                    style={{
                      width: '100%',
                      padding: '12px',
                      fontSize: '1rem',
                      backgroundColor: 'rgba(255, 255, 255, 0.05)',
                      border: '1px solid var(--primary-neon)',
                      borderRadius: '5px',
                      color: 'var(--text-primary)',
                      marginBottom: '20px'
                    }}
                  />
                </div>

                <div className="input-group">
                  <label className="input-label">GRID MATRIX SIZE ({boardSize}x{boardSize})</label>
                  <div className="sl-range-container">
                    <input
                      type="range"
                      min="6"
                      max="12"
                      value={boardSize}
                      onChange={e => setBoardSize(e.target.value)}
                      style={{ width: '100%' }}
                    />
                  </div>
                </div>

                {error && <p style={{ color: 'var(--error-neon)', marginBottom: '15px' }}>{error}</p>}

                <button className="btn-primary sl-btn-start" onClick={handleStartGame}>
                  INITIALIZE SYSTEM
                </button>
              </div>
            </div>

          </div>
        </div>
      )}

      {step === 2 && gameData && (
        <div className="game-section">
          <div className="glass-panel">
            <p>Target: Cell {gameData.boardSize ** 2}. Avoid Snakes (Red), Use Ladders (Green).</p>
          </div>

          {renderBoard()}

          {/* Algorithm Execution Times */}
          {gameData.algorithmTimes && (
            <div className="glass-panel" style={{ textAlign: 'center', padding: '20px', marginTop: '20px' }}>
              <h4 style={{ color: 'var(--primary-neon)', marginBottom: '10px' }}>Algorithm Performance</h4>
              <div style={{ display: 'flex', gap: '30px', justifyContent: 'center', flexWrap: 'wrap' }}>
                <div>
                  <p className="text-muted">BFS Algorithm</p>
                  <h3 style={{ color: 'var(--success-neon)' }}>{gameData.algorithmTimes.BFS} μs</h3>
                </div>
                <div>
                  <p className="text-muted">Dijkstra Algorithm</p>
                  <h3 style={{ color: 'var(--accent-neon)' }}>{gameData.algorithmTimes.Dijkstra} μs</h3>
                </div>
              </div>
            </div>
          )}

          {/* Question and Answer Choices */}
          {(
            <div className="glass-panel" style={{ textAlign: 'center', padding: '30px' }}>
              <h4 style={{ marginBottom: '20px', color: 'var(--text-muted)' }}>
                What is the minimum number of dice throws to reach the last cell?
              </h4>
              <div style={{ display: 'flex', gap: '15px', justifyContent: 'center', flexWrap: 'wrap' }}>
                {gameData.choices && gameData.choices.map((choice, idx) => (
                  <button
                    key={idx}
                    className="btn-primary"
                    onClick={() => {
                      setUserGuess(choice);
                      handleSubmitGuess(choice);
                    }}
                    style={{
                      fontSize: '1.5rem',
                      padding: '20px 40px',
                      minWidth: '100px',
                      background: 'rgba(0, 243, 255, 0.1)',
                      border: '2px solid var(--primary-neon)',
                      transition: 'all 0.3s'
                    }}
                  >
                    {choice}
                  </button>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {step === 3 && result && (
        <div className="glass-panel" style={{
          textAlign: 'center',
          borderColor: result.correct ? 'var(--success-neon)' : 'var(--error-neon)',
          borderWidth: '3px'
        }}>
          {result.correct ? (
            /* Success Screen */
            <>
              <div style={{
                padding: '20px',
                marginBottom: '20px',
                background: 'rgba(0, 255, 157, 0.1)',
                borderRadius: '8px'
              }}>
                <h2 style={{
                  color: 'var(--success-neon)',
                  fontSize: '2rem',
                  margin: 0
                }}>
                  🎉 CONGRATULATIONS!
                </h2>
                <p style={{ color: 'var(--text-muted)', marginTop: '10px' }}>You solved it correctly!</p>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginTop: '20px' }}>
                <div>
                  <p className="text-muted">Your Answer</p>
                  <h1 style={{ color: 'var(--success-neon)' }}>{result.userGuess}</h1>
                </div>
                <div>
                  <p className="text-muted">Correct Answer</p>
                  <h1 style={{ color: 'var(--success-neon)' }}>{result.minDiceThrows}</h1>
                </div>
              </div>

              <div style={{ display: 'flex', gap: '15px', justifyContent: 'center', marginTop: '30px' }}>
                <button className="btn-secondary" onClick={handleRestart}>Play Again</button>
                <button className="btn-primary" onClick={handleBackToMenu}>Back to Menu</button>
              </div>
            </>
          ) : (
            /* Failure Screen */
            <>
              <div style={{
                padding: '20px',
                marginBottom: '20px',
                background: 'rgba(255, 0, 85, 0.1)',
                borderRadius: '8px'
              }}>
                <h2 style={{
                  color: 'var(--error-neon)',
                  fontSize: '2rem',
                  margin: 0
                }}>
                  ❌ INCORRECT
                </h2>
                <p style={{ color: 'var(--text-muted)', marginTop: '10px' }}>Better luck next time!</p>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginTop: '20px' }}>
                <div>
                  <p className="text-muted">Your Answer</p>
                  <h1 style={{ color: 'var(--error-neon)' }}>{result.userGuess}</h1>
                </div>
                <div>
                  <p className="text-muted">Correct Answer</p>
                  <h1 style={{ color: 'var(--success-neon)' }}>{result.minDiceThrows}</h1>
                </div>
              </div>

              <div style={{ display: 'flex', gap: '15px', justifyContent: 'center', marginTop: '30px' }}>
                <button className="btn-secondary" onClick={handleRestart}>Try Again</button>
                <button className="btn-primary" onClick={handleBackToMenu}>Back to Menu</button>
              </div>
            </>
          )}
        </div>
      )}
    </div>
  );
}

export default SnakeAndLadder;
