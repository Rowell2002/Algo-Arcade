import { useNavigate } from "react-router-dom";
import { motion, useMotionValue, useSpring, useTransform } from "framer-motion";
import "./Menu.css";

function TiltCard({ game, onClick, variants }) {
  const x = useMotionValue(0);
  const y = useMotionValue(0);

  const rotateX = useTransform(y, [-100, 100], [10, -10]);
  const rotateY = useTransform(x, [-100, 100], [-10, 10]);

  const springConfig = { damping: 25, stiffness: 120, mass: 1.2 };
  const rotateXSpring = useSpring(rotateX, springConfig);
  const rotateYSpring = useSpring(rotateY, springConfig);

  const handleMouseMove = (e) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;
    // Calculate distance from center
    const moveX = e.clientX - centerX;
    const moveY = e.clientY - centerY;

    x.set(moveX);
    y.set(moveY);
  };

  const handleMouseLeave = () => {
    x.set(0);
    y.set(0);
  };

  return (
    <motion.div
      className="game-card glass-panel"
      variants={variants}
      initial="hidden"
      animate="show"
      onClick={onClick}
      onMouseMove={handleMouseMove}
      onMouseLeave={handleMouseLeave}
      style={{
        '--card-color': game.color,
        '--card-gradient': game.gradient,
        rotateX: rotateXSpring,
        rotateY: rotateYSpring,
        transformStyle: "preserve-3d"
      }}
      whileHover={{ scale: 1.05, zIndex: 10 }}
      whileTap={{ scale: 0.95 }}
    >
      <div className="card-glow"></div>
      <div className="card-gradient-bg"></div>
      <div className="card-content" style={{ transform: "translateZ(30px)" }}>
        <motion.div
          className="card-icon"
          style={{ transform: "translateZ(40px)" }}
        >
          <img src={game.icon} alt={game.name} />
        </motion.div>
        <motion.h3 style={{ transform: "translateZ(30px)" }}>
          {game.name}
        </motion.h3>
        <p style={{ transform: "translateZ(20px)" }}>{game.desc}</p>
      </div>
      <div className="card-footer" style={{ transform: "translateZ(25px)" }}>
        <span className="play-btn">LAUNCH MODULE →</span>
      </div>
    </motion.div>
  );
}

function Menu() {
  const navigate = useNavigate();

  const games = [
    {
      id: 1,
      name: "Snake & Ladder",
      path: "/snake-and-ladder",
      desc: "Classic chance game with BFS/Dijkstra visualization.",
      color: "var(--primary-neon)",
      gradient: "var(--gradient-blue)",
      icon: "/assets/icons/snake-ladder.png"
    },
    {
      id: 2,
      name: "Traffic Sim",
      path: "/traffic-simulation",
      desc: "Optimize flow with Max-Flow Min-Cut algorithms.",
      color: "#ff0055",
      gradient: "var(--gradient-pink)",
      icon: "/assets/icons/traffic.png"
    },
    {
      id: 3,
      name: "Traveling Salesman",
      path: "/traveling-salesman",
      desc: "Find the shortest route. NP-Hard problem visualization.",
      color: "#ffd700",
      gradient: "var(--gradient-sunset)",
      icon: "/assets/icons/tsp.png"
    },
    {
      id: 4,
      name: "Tower of Hanoi",
      path: "/tower-of-hanoi",
      desc: "Recursive puzzle solving with 3 or 4 pegs.",
      color: "#00ff9d",
      gradient: "var(--gradient-green)",
      icon: "/assets/icons/hanoi.png"
    },
    {
      id: 5,
      name: "Eight Queens",
      path: "/eight-queens",
      desc: "Place 8 queens on a board safely using Backtracking.",
      color: "#bc13fe",
      gradient: "var(--gradient-purple)",
      icon: "/assets/icons/queens.png"
    }
  ];

  const containerVariants = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
        delayChildren: 0.2
      }
    }
  };

  const cardVariants = {
    hidden: {
      opacity: 0,
      y: 50,
      rotateX: 10
    },
    show: {
      opacity: 1,
      y: 0,
      rotateX: 0,
      transition: {
        type: "spring",
        bounce: 0.4,
        duration: 0.8
      }
    }
  };

  return (
    <div className="menu-container page">
      <div
        className="hero-section"
        style={{
          backgroundImage: "linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.6)), url('/assets/gaming_hero_bg.png')"
        }}
      >
        <div className="hero-animated-bg">
          <div className="cyber-grid"></div>
          <div className="cyber-particles">
            <span></span><span></span><span></span><span></span><span></span>
            <span></span><span></span><span></span><span></span><span></span>
          </div>
        </div>
        <div className="hero-overlay"></div>
        <h1 className="title">
          ALGO <span>ARCADE</span>
        </h1>
        <p className="subtitle">Interactive Data Structure & Algorithm Visualization</p>
        <div className="stats-bar">
          <div className="stat-item">
            <span className="stat-value">5</span>
            <span className="stat-label">Games</span>
          </div>
          <div className="stat-divider"></div>
          <div className="stat-item">
            <span className="stat-value">10+</span>
            <span className="stat-label">Algorithms</span>
          </div>
          <div className="stat-divider"></div>
          <div className="stat-item">
            <span className="stat-value">∞</span>
            <span className="stat-label">Learning</span>
          </div>
        </div>
      </div>

      <motion.div
        className="game-grid"
        variants={containerVariants}
        initial="hidden"
        animate="show"
      >
        {games.map((game) => (
          <TiltCard
            key={game.id}
            game={game}
            onClick={() => navigate(game.path)}
            variants={cardVariants}
          />
        ))}
      </motion.div>
    </div>
  );
}

export default Menu;
