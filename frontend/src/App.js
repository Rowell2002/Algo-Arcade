import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import "./App.css";
import Menu from "./components/Menu";
import AboutUs from "./pages/AboutUs";
import AlgorithmComparison from "./pages/AlgorithmComparison";
import ContactUs from "./pages/ContactUs";
import EightQueens from "./pages/EightQueens";
import SnakeAndLadder from "./pages/SnakeAndLadder";
import TowerOfHanoi from "./pages/TowerOfHanoi";
import TrafficSimulation from "./pages/TrafficSimulation";
import TravelingSalesman from "./pages/TravelingSalesman";

import CursorFollower from "./components/CursorFollower";
import Footer from "./components/Footer";
import Header from "./components/Header";
import ParticleBackground from "./components/ParticleBackground";
import ToastContainer from "./components/ToastContainer";

function App() {
  return (
    <Router>
      <div className="App">
        <ParticleBackground />
        <CursorFollower />
        <ToastContainer />

        <Header />

        <main className="main-content">
          <Routes>
            <Route path="/" element={<Menu />} />
            <Route path="/snake-and-ladder" element={<SnakeAndLadder />} />
            <Route path="/traffic-simulation" element={<TrafficSimulation />} />
            <Route path="/traveling-salesman" element={<TravelingSalesman />} />
            <Route path="/tower-of-hanoi" element={<TowerOfHanoi />} />
            <Route path="/eight-queens" element={<EightQueens />} />
            <Route path="/algorithm-comparison" element={<AlgorithmComparison />} />
            <Route path="/about-us" element={<AboutUs />} />
            <Route path="/contact-us" element={<ContactUs />} />
          </Routes>
        </main>

        <Footer />
      </div>
    </Router>
  );
}

export default App;
