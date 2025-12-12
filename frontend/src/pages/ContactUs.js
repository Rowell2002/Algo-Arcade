import { useState, useEffect, useRef } from 'react';
import { motion } from 'framer-motion';
import './ContactUs.css';

const ContactUs = () => {
    // --- Form State ---
    const [formData, setFormData] = useState({ name: '', email: '', subject: '', message: '' });
    const [formStatus, setFormStatus] = useState('idle'); // idle, sending, success

    // --- Chat State ---
    const [messages, setMessages] = useState([
        { id: 1, sender: 'bot', text: 'System Online. Initializing support protocol...' },
        { id: 2, sender: 'bot', text: 'Greetings, Agent. How can I assist you today?' }
    ]);
    const [inputMsg, setInputMsg] = useState('');
    const chatEndRef = useRef(null);

    // Auto-scroll chat
    useEffect(() => {
        chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    // Handle Form Change
    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // Handle Form Submit
    const handleSubmit = (e) => {
        e.preventDefault();
        setFormStatus('sending');
        // Simulate network request
        setTimeout(() => {
            setFormStatus('success');
            setFormData({ name: '', email: '', subject: '', message: '' });
            setTimeout(() => setFormStatus('idle'), 3000);
        }, 1500);
    };

    // Handle Chat Send
    const handleSendMessage = (e) => {
        e.preventDefault();
        if (!inputMsg.trim()) return;

        // User Message
        const newMsg = { id: Date.now(), sender: 'user', text: inputMsg };
        setMessages(prev => [...prev, newMsg]);
        setInputMsg('');

        // Bot Auto-Reply (Simulation)
        setTimeout(() => {
            const botResponses = [
                "Acknowledged. Accessing database...",
                "Your query has been logged. Priority: High.",
                "Analyzing input parameters...",
                "Please hold while I establish a secure link.",
                "Signal received. Decoding message..."
            ];
            const randomResponse = botResponses[Math.floor(Math.random() * botResponses.length)];
            setMessages(prev => [...prev, { id: Date.now() + 1, sender: 'bot', text: randomResponse }]);
        }, 1000);
    };

    return (
        <div className="page contact-us-page">
            {/* Custom Hero Section */}
            <div className="hero-section" style={{
                backgroundImage: "linear-gradient(rgba(0,0,0,0.3), rgba(0,0,0,0.8)), url('/assets/images/contact_hero_cyberpunk.png')",
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                height: '350px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                flexDirection: 'column',
                marginBottom: '40px',
                borderRadius: '0 0 20px 20px',
                boxShadow: '0 10px 30px rgba(255, 0, 85, 0.2)'
            }}>
                <motion.h1
                    className="title"
                    style={{ fontSize: '3.5rem', textShadow: '0 0 20px #ff0055' }}
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.8 }}
                >
                    GLOBAL <span style={{ color: '#ff0055' }}>COMMS</span>
                </motion.h1>
                <p className="subtitle" style={{ fontSize: '1.2rem', marginTop: '10px', color: '#00f3ff' }}>Establish A Secure Connection</p>
            </div>

            <div className="contact-content">
                {/* Left: Enhanced Form */}
                <motion.div
                    className="glass-panel contact-form-container"
                    initial={{ opacity: 0, x: -50 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: 0.2 }}
                >
                    <h2>TRANSMIT MESSAGE</h2>
                    <form onSubmit={handleSubmit} className="contact-form">
                        <div className="form-group">
                            <label htmlFor="name">AGENT IDENTITY (Name)</label>
                            <input
                                type="text"
                                id="name"
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                required
                                placeholder="Enter your codename..."
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="email">COMMS FREQUENCY (Email)</label>
                            <input
                                type="email"
                                id="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                required
                                placeholder="agent@example.com"
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="subject">SUBJECT PROTOCOL</label>
                            <input
                                type="text"
                                id="subject"
                                name="subject"
                                value={formData.subject}
                                onChange={handleChange}
                                required
                                placeholder="Purpose of contact..."
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="message">ENCRYPTED DATA (Message)</label>
                            <textarea
                                id="message"
                                name="message"
                                value={formData.message}
                                onChange={handleChange}
                                required
                                placeholder="Type your message here..."
                                rows="5"
                            ></textarea>
                        </div>

                        <button type="submit" className="submit-btn">
                            {formStatus === 'sending' ? 'TRANSMITTING...' : formStatus === 'success' ? 'TRANSMISSION COMPLETE' : 'INITIATE UPLOAD'}
                        </button>
                    </form>
                </motion.div>

                {/* Right: Info & Live Chat */}
                <div className="contact-info-side">

                    {/* Live Chat Simulation */}
                    <motion.div
                        className="chat-widget"
                        initial={{ opacity: 0, x: 50 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ delay: 0.4 }}
                    >
                        <div className="chat-header">
                            <div className="status-dot"></div>
                            <span style={{ color: '#00ff9d', fontWeight: 'bold' }}>LIVE SUPPORT (AI)</span>
                        </div>
                        <div className="chat-messages">
                            {messages.map(msg => (
                                <div key={msg.id} className={`message ${msg.sender}`}>
                                    {msg.text}
                                </div>
                            ))}
                            <div ref={chatEndRef} />
                        </div>
                        <form className="chat-input-area" onSubmit={handleSendMessage}>
                            <input
                                type="text"
                                placeholder="Type a message..."
                                value={inputMsg}
                                onChange={(e) => setInputMsg(e.target.value)}
                            />
                            <button type="submit" className="send-btn">➤</button>
                        </form>
                    </motion.div>

                    {/* Quick Info Cards */}
                    <motion.div
                        className="info-card glass-panel"
                        initial={{ opacity: 0, y: 30 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.6 }}
                    >
                        <h3>DIRECT LINES</h3>
                        <p>HQ Terminal: <a href="tel:+1234567890">+1 (234) 567-890</a></p>
                        <p>Data Stream: <a href="mailto:contact@algoarcade.com">contact@algoarcade.com</a></p>
                        <p>Coordinates: Sector 7G, Cyber District</p>

                        <div className="social-nodes">
                            <a href="#" className="node-link" title="GitHub">
                                <img src="/assets/icons/github_neon.png" alt="GitHub" />
                            </a>
                            <a href="#" className="node-link" title="Twitter">
                                <img src="/assets/icons/twitter_neon.png" alt="Twitter" />
                            </a>
                            <a href="#" className="node-link" title="LinkedIn">
                                <img src="/assets/icons/linkedin_neon.png" alt="LinkedIn" />
                            </a>
                        </div>
                    </motion.div>
                </div>
            </div>
        </div>
    );
};

export default ContactUs;
