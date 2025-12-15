import { useEffect } from 'react';
import './Toast.css';

function Toast({ message, type = 'info', duration = 3000, onClose }) {
    useEffect(() => {
        if (duration > 0) {
            const timer = setTimeout(() => {
                if (onClose) onClose();
            }, duration);
            return () => clearTimeout(timer);
        }
    }, [duration, onClose]);

    const getIcon = () => {
        switch (type) {
            case 'success': return '✓';
            case 'error': return '✕';
            case 'warning': return '⚠';
            default: return 'ℹ';
        }
    };

    return (
        <div className={`toast toast-${type}`}>
            <div className="toast-icon">{getIcon()}</div>
            <div className="toast-message">{message}</div>
            {onClose && (
                <button className="toast-close" onClick={onClose}>
                    ×
                </button>
            )}
        </div>
    );
}

export default Toast;
