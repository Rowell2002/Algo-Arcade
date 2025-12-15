import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { motion } from 'framer-motion';
import './Header.css';

const Header = () => {
    const location = useLocation();
    const [gamesOpen, setGamesOpen] = useState(false);

    const isActive = (path) => location.pathname === path;

    const games = [
        { name: "Snake & Ladder", path: "/snake-and-ladder" },
        { name: "Traffic Simulation", path: "/traffic-simulation" },
        { name: "Traveling Salesman", path: "/traveling-salesman" },
        { name: "Tower of Hanoi", path: "/tower-of-hanoi" },
        { name: "Eight Queens", path: "/eight-queens" }
    ];

    return (
        <motion.header
            className="site-header"
            initial={{ y: -100 }}
            animate={{ y: 0 }}
            transition={{ duration: 0.5, ease: "easeOut" }}
        >
            <div className="header-content">
                <Link to="/" className="branding">
                    ALGO <span>ARCADE</span>
                </Link>

                <nav className="main-nav">
                    <Link to="/" className={`nav-link ${isActive('/') ? 'active' : ''}`}>
                        Home
                    </Link>

                    <div
                        className="nav-item-dropdown"
                        onMouseEnter={() => setGamesOpen(true)}
                        onMouseLeave={() => setGamesOpen(false)}
                    >
                        <span className={`nav-link ${gamesOpen ? 'active' : ''}`}>
                            Games ▾
                        </span>
                        <div className={`dropdown-menu ${gamesOpen ? 'show' : ''}`}>
                            {games.map(game => (
                                <Link key={game.path} to={game.path} className="dropdown-item">
                                    {game.name}
                                </Link>
                            ))}
                        </div>
                    </div>

                    <Link to="/algorithm-comparison" className={`nav-link ${isActive('/algorithm-comparison') ? 'active' : ''}`}>
                        Comparison
                    </Link>

                    <Link to="/about-us" className={`nav-link ${isActive('/about-us') ? 'active' : ''}`}>
                        About Us
                    </Link>

                    <Link to="/contact-us" className={`nav-link ${isActive('/contact-us') ? 'active' : ''}`}>
                        Contact Us
                    </Link>
                </nav>
            </div>
        </motion.header>
    );
};

export default Header;
