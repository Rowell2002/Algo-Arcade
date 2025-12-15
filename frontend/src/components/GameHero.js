import './GameHero.css';

function GameHero({ title, subtitle, icon, gradient, color }) {
    return (
        <div className="game-hero" style={{ '--hero-gradient': gradient, '--hero-color': color }}>
            <div className="hero-gradient-bg"></div>
            <div className="hero-content">
                {icon && (
                    <div className="hero-icon">
                        <img src={icon} alt={title} />
                    </div>
                )}
                <h1 className="hero-title">{title}</h1>
                {subtitle && <p className="hero-subtitle">{subtitle}</p>}
            </div>
            <div className="hero-glow"></div>
        </div>
    );
}

export default GameHero;
