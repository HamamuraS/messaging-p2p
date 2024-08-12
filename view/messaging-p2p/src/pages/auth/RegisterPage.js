import { useNavigate } from "react-router-dom";
import './Forms.css';
import React, { useRef, useState } from 'react';

function RegisterPage({handleRegister}) {
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const inputRef = useRef(null);
    
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            setErrorMessage('Passwords do not match.');
            return;
        }
        const response = await handleRegister({ username, email, password });
        if (!response.ok) {
            setErrorMessage(await response.text());
            return;
        }
        navigate('/login');
    };
    
    const handleToggleClick = () => {
        if (inputRef.current) {
            const cursorPosition = inputRef.current.selectionStart;
            setShowPassword(!showPassword);
            setTimeout(() => {
                if (inputRef.current) {
                    inputRef.current.setSelectionRange(cursorPosition, cursorPosition);
                }
            }, 0);
        }
    };
    
    return (
        <div className="form-page-container">
        <form className="form" onSubmit={handleSubmit}>
            <h2>Register</h2>
            <div className="form-group">
            <label htmlFor="username">Username:</label>
            <input
                type="text"
                id="username"
                value={username}
                onChange={
                    (e) => {
                        setUsername(e.target.value);
                        errorMessage && setErrorMessage('');
                    }
                }
                required
            />
            </div>
            <div className="form-group">
            <label htmlFor="email">Email:</label>
            <input
                type="email"
                id="email"
                value={email}
                onChange={
                    (e) => {
                        setEmail(e.target.value);
                        errorMessage && setErrorMessage('');
                    }
                }
                required
            />
            </div>
            <div className="form-group">
            <label htmlFor="password">Password:</label>
            <div style={{ display: 'flex', alignItems: 'center' }}>
            <input 
                ref={inputRef}
                type={showPassword? "text" : "password"}
                id="password"
                value={password}
                onChange={
                    (e) => {
                        setPassword(e.target.value);
                        errorMessage && setErrorMessage('');
                    }
                }
                required
            />
            <button
                type="button"
                className="toggle-password"
                onMouseDown={(e) => e.preventDefault()}
                onClick={handleToggleClick}
            >
                {showPassword ? "🙈" : "👁️"}
            </button>
            </div>
            
            </div>
            <div className="form-group">
            <label htmlFor="password">Confirm your password:</label>
            <input
                type="password"
                id="confirm-password"
                value={confirmPassword}
                onChange={
                    (e) => {
                        setConfirmPassword(e.target.value);
                        setErrorMessage('');
                    }
                }
                required
            />
            </div>
            {errorMessage && <p className="error-message">{errorMessage}</p>}
            <button type="submit" className="submit-button">Register</button>
            <p className="extern-link">
                Already have an account? <a href="/login">Login here</a>
            </p>
        </form>
        </div>
    );
}

export default RegisterPage;