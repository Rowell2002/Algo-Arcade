import { useCallback, useState } from 'react';
import Toast from './Toast';

let toastId = 0;

function ToastContainer() {
    const [toasts, setToasts] = useState([]);

    const addToast = useCallback((message, type = 'info', duration = 3000) => {
        const id = toastId++;
        setToasts(prev => [...prev, { id, message, type, duration }]);
    }, []);

    const removeToast = useCallback((id) => {
        setToasts(prev => prev.filter(toast => toast.id !== id));
    }, []);

    // Expose addToast globally
    if (typeof window !== 'undefined') {
        window.showToast = addToast;
    }

    return (
        <div style={{ position: 'fixed', bottom: 0, right: 0, zIndex: 10001 }}>
            {toasts.map((toast, index) => (
                <div
                    key={toast.id}
                    style={{
                        marginBottom: index > 0 ? '10px' : '0',
                    }}
                >
                    <Toast
                        message={toast.message}
                        type={toast.type}
                        duration={toast.duration}
                        onClose={() => removeToast(toast.id)}
                    />
                </div>
            ))}
        </div>
    );
}

export default ToastContainer;
