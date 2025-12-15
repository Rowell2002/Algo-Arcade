import { useEffect, useRef } from 'react';

function Confetti({ active, onComplete }) {
    const canvasRef = useRef(null);

    useEffect(() => {
        if (!active) return;

        const canvas = canvasRef.current;
        if (!canvas) return;

        const ctx = canvas.getContext('2d');
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;

        const particles = [];
        const particleCount = 150;
        const colors = [
            '#00f3ff', // cyan
            '#bc13fe', // purple
            '#00ff9d', // green
            '#ffd700', // gold
            '#ff0055', // pink
            '#ffffff', // white
        ];

        class ConfettiParticle {
            constructor() {
                this.x = Math.random() * canvas.width;
                this.y = -10;
                this.size = Math.random() * 8 + 4;
                this.speedY = Math.random() * 3 + 2;
                this.speedX = Math.random() * 4 - 2;
                this.color = colors[Math.floor(Math.random() * colors.length)];
                this.rotation = Math.random() * 360;
                this.rotationSpeed = Math.random() * 10 - 5;
                this.gravity = 0.15;
                this.opacity = 1;
            }

            update() {
                this.speedY += this.gravity;
                this.x += this.speedX;
                this.y += this.speedY;
                this.rotation += this.rotationSpeed;

                // Fade out near bottom
                if (this.y > canvas.height - 100) {
                    this.opacity -= 0.02;
                }
            }

            draw() {
                ctx.save();
                ctx.globalAlpha = this.opacity;
                ctx.translate(this.x, this.y);
                ctx.rotate((this.rotation * Math.PI) / 180);
                ctx.fillStyle = this.color;

                // Draw rectangle confetti
                ctx.fillRect(-this.size / 2, -this.size / 2, this.size, this.size * 1.5);

                ctx.restore();
            }

            isDead() {
                return this.y > canvas.height || this.opacity <= 0;
            }
        }

        // Create particles
        for (let i = 0; i < particleCount; i++) {
            setTimeout(() => {
                particles.push(new ConfettiParticle());
            }, i * 10);
        }

        let animationId;
        function animate() {
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            particles.forEach((particle, index) => {
                particle.update();
                particle.draw();

                if (particle.isDead()) {
                    particles.splice(index, 1);
                }
            });

            if (particles.length > 0) {
                animationId = requestAnimationFrame(animate);
            } else {
                // Animation complete
                if (onComplete) onComplete();
            }
        }

        animate();

        return () => {
            if (animationId) cancelAnimationFrame(animationId);
        };
    }, [active, onComplete]);

    if (!active) return null;

    return (
        <canvas
            ref={canvasRef}
            style={{
                position: 'fixed',
                top: 0,
                left: 0,
                width: '100%',
                height: '100%',
                zIndex: 10000,
                pointerEvents: 'none',
            }}
        />
    );
}

export default Confetti;
