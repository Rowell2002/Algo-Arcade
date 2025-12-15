import { useLocation, useNavigate } from "react-router-dom";
import "../App.css";

function HomeButton() {
    const navigate = useNavigate();
    const location = useLocation();

    // Hide on Home Page (Root)
    if (location.pathname === "/") return null;

    return (
        <button
            className="home-btn"
            onClick={() => navigate("/")}
            title="Return to Arcade Menu"
        >
            ⌂ MENU
        </button>
    );
}

export default HomeButton;
