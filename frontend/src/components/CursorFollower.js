import { useEffect, useState } from "react";
import "../App.css";

function CursorFollower() {
    const [position, setPosition] = useState({ x: 0, y: 0 });
    const [trail, setTrail] = useState([]);

    useEffect(() => {
        const handleMouseMove = (e) => {
            const newPos = { x: e.clientX, y: e.clientY };
            setPosition(newPos);

            // Add to trail
            setTrail(prev => {
                const newTrail = [...prev, newPos];
                // Keep only last 5 positions
                return newTrail.slice(-5);
            });
        };

        window.addEventListener("mousemove", handleMouseMove);
        return () => window.removeEventListener("mousemove", handleMouseMove);
    }, []);

    return (
        <>
            {/* Main cursor glow */}
            <div
                className="cursor-follower"
                style={{
                    left: `${position.x}px`,
                    top: `${position.y}px`,
                }}
            />

            {/* Trail effect */}
            {trail.map((pos, index) => (
                <div
                    key={index}
                    className="cursor-trail"
                    style={{
                        left: `${pos.x}px`,
                        top: `${pos.y}px`,
                        opacity: (index + 1) / trail.length * 0.3,
                        transform: `translate(-50%, -50%) scale(${(index + 1) / trail.length})`,
                    }}
                />
            ))}
        </>
    );
}

export default CursorFollower;
