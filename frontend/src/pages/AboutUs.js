import { motion } from "framer-motion";
import './AboutUs.css';

const AboutUs = () => {
    const teamMembers = [
        { id: 1, name: "Dhanushka Jayakody", role: "Full Stack Developer", img: "/assets/images/member_1.png" },
        { id: 2, name: "Anushka Dahanayake", role: "Full Stack Developer", img: "/assets/images/member_2.PNG" },
        { id: 3, name: "Chethana Rowell", role: "Full Stack Developer", img: "/assets/images/member_3.png" },
        { id: 4, name: "Mileesha Fernando", role: "Full Stack Developer", img: "/assets/images/member_4.png" }
    ];

    const cardVariants = {
        hidden: { opacity: 0, y: 50 },
        visible: (i) => ({
            opacity: 1,
            y: 0,
            transition: {
                delay: i * 0.1,
                duration: 0.5,
                type: "spring",
                stiffness: 100
            }
        })
    };

    return (
        <div className="page about-us-page">
            <div className="hero-section" style={{
                backgroundImage: "linear-gradient(rgba(0,0,0,0.3), rgba(0,0,0,0.8)), url('/assets/images/about_hero_cyberpunk.png')",
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                height: '400px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                flexDirection: 'column',
                marginBottom: '40px',
                borderRadius: '0 0 20px 20px',
                boxShadow: '0 10px 30px rgba(0, 243, 255, 0.2)'
            }}>
                <h1 className="title" style={{ fontSize: '4rem', textShadow: '0 0 20px #00f3ff' }}>
                    ABOUT <span style={{ color: '#00f3ff' }}>US</span>
                </h1>
                <p className="subtitle" style={{ fontSize: '1.5rem', marginTop: '10px' }}>The Architects of the Digital Realm</p>
            </div>


            <div className="glass-panel text-content" style={{ maxWidth: '800px', margin: '0 auto 50px auto', padding: '40px' }}>
                <h2 style={{ color: '#00f3ff', marginBottom: '20px' }}>Our Mission</h2>
                <p style={{ marginBottom: '15px', lineHeight: '1.6' }}>
                    Algo Arcade was born from a passion to make complex computer science concepts accessible, interactive, and fun.
                    We believe that the best way to understand algorithms is to see them in action.
                    Through gamification, we turn abstract logic into tangible experiences.
                </p>
                <p style={{ lineHeight: '1.6' }}>
                    Whether you are a student preparing for interviews, a teacher looking for visual aids, or just a curious mind,
                    Algo Arcade provides a playground to explore the beauty of computation.
                </p>
            </div>

            <h2 className="section-title" style={{ textAlign: 'center', marginBottom: '40px', fontSize: '2.5rem' }}>Meet The <span style={{ color: '#bc13fe' }}>Team</span></h2>

            <div className="team-grid" style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
                gap: '30px',
                padding: '0 20px 60px 20px',
                maxWidth: '1200px',
                margin: '0 auto'
            }}>
                {teamMembers.map((member, i) => (
                    <motion.div
                        key={member.id}
                        className="team-card glass-panel"
                        custom={i}
                        initial="hidden"
                        animate="visible"
                        variants={cardVariants}
                        whileHover={{
                            scale: 1.05,
                            boxShadow: "0 0 25px rgba(0, 243, 255, 0.4)",
                            borderColor: "#00f3ff"
                        }}
                        style={{
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                            textAlign: 'center',
                            padding: '30px',
                            background: 'linear-gradient(135deg, rgba(20, 20, 30, 0.8), rgba(0, 0, 0, 0.9))',
                            border: '1px solid rgba(0, 243, 255, 0.15)',
                            boxShadow: '0 8px 32px 0 rgba(0, 0, 0, 0.37)'
                        }}
                    >
                        <div className="profile-placeholder" style={{
                            width: '150px',
                            height: '150px',
                            borderRadius: '50%',
                            overflow: 'hidden',
                            marginBottom: '20px',
                            border: '3px solid #00f3ff',
                            boxShadow: '0 0 15px rgba(0, 243, 255, 0.3)'
                        }}>
                            <img src={member.img} alt={member.name} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                        </div>
                        <h3 style={{ fontSize: '1.5rem', marginBottom: '10px' }}>{member.name}</h3>
                        <p className="role" style={{ color: '#bc13fe', fontWeight: 'bold', letterSpacing: '1px' }}>{member.role}</p>
                    </motion.div>
                ))}
            </div>
        </div>
    );
};

export default AboutUs;
